<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import GameGrid from './gamegrid.vue'
import Keyboard from './keyboard.vue'
import { saveScoreAPI } from '../api.js'

const wsConnected = ref(false)
const playerScores = ref({})
const totalGuesses = ref(0)
const ws = ref(null)
const reconnectAttempts = ref(0)
const maxReconnectAttempts = 5

function resetGameState() {
  guesses.value = []
  guessedBy.value = []
  currentGuess.value = ''
  keyboardColors.value = {}
  gameActive.value = true
  totalGuesses.value = 0
  playerScores.value = {}
}

// WebSocket Verbindung
function connectWebSocket() {
  try {
    ws.value = new WebSocket('ws://localhost:3000')
    
    ws.value.onopen = () => {
      console.log('🔗 WebSocket verbunden')
      wsConnected.value = true
      reconnectAttempts.value = 0
      
      // Spieler anmelden
      ws.value.send(JSON.stringify({
        type: 'playerJoin',
        user: props.user.user
      }))
    }
    
    ws.value.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        handleWebSocketMessage(data)
      } catch (error) {
        console.error('Fehler beim Parsen der WebSocket-Nachricht:', error)
      }
    }
    
    ws.value.onclose = () => {
      console.log('🔌 WebSocket Verbindung geschlossen')
      wsConnected.value = false
      
      // Automatisches Reconnect
      if (reconnectAttempts.value < maxReconnectAttempts) {
        reconnectAttempts.value++
        console.log(`🔄 Reconnect Versuch ${reconnectAttempts.value}/${maxReconnectAttempts}`)
        setTimeout(connectWebSocket, 3000)
      }
    }
    
    ws.value.onerror = (error) => {
      console.error('❌ WebSocket Fehler:', error)
      wsConnected.value = false
    }
    
  } catch (error) {
    console.error('Fehler beim Verbinden mit WebSocket:', error)
    wsConnected.value = false
  }
}

function closeWebSocket() {
  if (ws.value) {
    ws.value.close()
    ws.value = null
  }
  wsConnected.value = false
}

function sendWebSocketMessage(message) {
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send(JSON.stringify(message))
  }
}

// WebSocket Message Handler
function handleWebSocketMessage(data) {
  switch (data.type) {
    case 'welcome':
      console.log('🎉 Willkommen:', data)
      // Game State anfordern
      sendWebSocketMessage({ type: 'requestGameState' })
      break
      
    case 'gameState':
      console.log('🔄 Game State erhalten:', data)
      if (data.currentWord && data.gameActive) {
        solution.value = data.currentWord
        gameActive.value = true
      } else {
        gameActive.value = false
      }
      
      if (data.timeRemaining !== undefined) timer.value = data.timeRemaining
      if (data.roundNumber) roundNumber.value = data.roundNumber
      if (data.lastWord) lastRoundSolution.value = data.lastWord
      if (data.players) connectedUsers.value = data.players.map(p => p.name)
      if (data.guesses) {
        guessedBy.value = data.guesses.map(g => ({ 
          user: g.user, 
          guess: g.guess,
          score: g.score || 0,
          correct: g.correct || false
        }))
        guesses.value = data.guesses.map(g => g.guess)
        totalGuesses.value = data.guesses.length
      }
      if (data.playerScores) playerScores.value = data.playerScores
      break
      
    case 'newRound':
      console.log('🆕 Neue Runde gestartet:', data)
      resetGameState()
      
      if (data.lastWord) {
        lastRoundSolution.value = data.lastWord
        setTimeout(() => {
          alert(`🎯 Neue Runde ${data.roundNumber}!\n${data.lastWord ? `Letztes Wort: ${data.lastWord}` : 'Erste Runde'}`)
        }, 100)
      }
      
      if (data.word) {
        solution.value = data.word
        gameActive.value = true
      }
      
      if (data.duration) timer.value = data.duration
      roundNumber.value = data.roundNumber
      break
      
    case 'roundEnded':
      console.log('🏁 Runde beendet:', data)
      gameActive.value = false
      
      if (data.playerScores) playerScores.value = data.playerScores
      
      // Score an Backend senden
      const playerScore = data.playerScores[props.user.user]
      if (playerScore && playerScore.roundScore > 0) {
        saveScoreAPI(props.user.user, playerScore.roundScore).catch(error => {
          console.error('Fehler beim Speichern des Scores:', error)
        })
      }
      
      let message = `🏁 Runde ${data.roundNumber} beendet!\n\nLösung: ${data.solution}\n`
      
      if (data.reason === 'solved') message += `✅ Wort wurde erraten!`
      else if (data.reason === 'timeout') message += `⏰ Zeit abgelaufen!`
      else if (data.reason === 'max_guesses') message += `🚫 6 Versuche erreicht!`
      
      if (playerScore) {
        message += `\n\n🏆 Deine Punkte diese Runde: ${playerScore.roundScore}`
        message += `\n📊 Gesamtpunkte: ${playerScore.totalScore}`
      }
      
      setTimeout(() => alert(message), 100)
      break
      
    case 'timer':
      timer.value = data.secondsLeft
      break
      
    case 'guess':
      console.log('💭 Guess erhalten:', data)
      const { guess, user, score, correct, totalGuessNumber } = data
      
      if (!guessedBy.value.some(g => g.user === user && g.guess === guess)) {
        guessedBy.value.push({ user, guess, score: score || 0, correct: correct || false })
      }
      
      if (!guesses.value.includes(guess)) guesses.value.push(guess)
      if (totalGuessNumber) totalGuesses.value = totalGuessNumber
      
      if (user === props.user.user) updateKeyboardColors(guess)
      break
      
    case 'correctGuess':
      console.log('🎯 Korrekter Versuch:', data)
      setTimeout(() => {
        alert(`🎉 ${data.user} hat das Wort erraten: ${data.word}!\n🏆 Punkte: ${data.score || 0}`)
      }, 500)
      break
      
    case 'playerList':
      console.log('👥 Spielerliste aktualisiert:', data.players)
      connectedUsers.value = data.players.map(p => ({ 
        name: p.name, 
        guessCount: p.guessCount,
        maxGuesses: p.maxGuesses,
        roundScore: p.roundScore || 0,
        totalScore: p.totalScore || 0
      }))
      break
      
    case 'userJoined':
      console.log('➕ User beigetreten:', data.username)
      break
      
    case 'userLeft':
      console.log('➖ User verlassen:', data.username)
      connectedUsers.value = connectedUsers.value.filter(u => u.name !== data.username)
      guessedBy.value = guessedBy.value.filter(g => g.user !== data.username)
      break
      
    case 'error':
      console.error('❌ WebSocket Fehler:', data.message)
      alert('❌ Fehler: ' + data.message)
      break
      
    default:
      console.warn('Unbekannter WebSocket Event:', data.type)
  }
}

async function submitGuess() {
  if (currentGuess.value.length !== GUESS_LENGTH || isGameOver.value || !canMakeGuess.value) {
    if (!canMakeGuess.value) {
      if (!wsConnected.value) {
        alert('Keine Verbindung zum Server!')
      } else if (totalGuesses.value >= 6) {
        alert('Maximale Gesamtanzahl von 6 Versuchen erreicht!')
      } else {
        alert(`Du hast keine Versuche mehr! (${playerGuessCount.value}/${maxGuessesForPlayer.value})`)
      }
    }
    return
  }
  
  if (!wsConnected.value) {
    alert('Keine Verbindung zum Server! Versuche es später erneut.')
    return
  }
  
  try {
    sendWebSocketMessage({
      type: 'guess',
      guess: currentGuess.value,
      user: props.user.user
    })
    currentGuess.value = ''
  } catch (error) {
    console.error('Fehler beim Abgeben des Rateversuchs:', error)
  }
}

function formatTime(seconds) {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

onMounted(async () => {
  console.log('🎮 Gamescreen wird geladen...')
  connectWebSocket()
  
  window.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  closeWebSocket()
  window.removeEventListener('keydown', handleKeyPress)
})
</script>