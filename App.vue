@@ .. @@
 <script setup>
 import { ref, computed } from 'vue'
-import { getHighscoresAPI, setUserActiveAPI, deleteUserAPI, changePasswordAPI, getUsersAPI} from './api.js'
+import { getHighscoresAPI, setUserActiveAPI, deleteUserAPI, changePasswordAPI, getUsersAPI, addWordAPI, getWordsAPI, deleteWordAPI } from './api.js'
 import GameScreen from './components/gamescreen.vue'
 import StartScreen from './components/startscreen.vue'

@@ .. @@
 // Admin-Zustand
 const pendingUsers = ref([]) // Benutzer die auf Freischaltung warten
+const allUsers = ref([]) // Alle Benutzer
 const selectedUsers = ref([]) // Ausgewählte Benutzer für Freischaltung/Löschung
 const adminMessage = ref('')
 const adminError = ref('')

+// Wörter-Verwaltung
+const words = ref([])
+const newWord = ref('')
+const selectedWords = ref([])

@@ .. @@
 async function loadPendingUsers() {
   if (!currentUser.value || currentUser.value.role !== 'admin') return
   try {
-    const response = await getUsersAPI(currentUser.value.user)
+    const response = await getUsersAPI()
+    allUsers.value = response.data
     pendingUsers.value = response.data.filter(user => !user.active)
     adminError.value = ''
   } catch (err) {
-    adminError.value = err.error || err
+    adminError.value = err.error || err.message || 'Fehler beim Laden der Benutzer'
   }
 }

+async function loadWords() {
+  try {
+    const response = await getWordsAPI()
+    words.value = response.data
+    adminError.value = ''
+  } catch (err) {
+    adminError.value = err.error || err.message || 'Fehler beim Laden der Wörter'
+  }
+}
+
+async function addWord() {
+  if (!newWord.value || newWord.value.length < 4) {
+    adminError.value = 'Wort muss mindestens 4 Buchstaben haben'
+    return
+  }
+  
+  try {
+    await addWordAPI(newWord.value)
+    adminMessage.value = 'Wort hinzugefügt'
+    newWord.value = ''
+    await loadWords()
+  } catch (err) {
+    adminError.value = err.error || err.message || 'Fehler beim Hinzufügen des Wortes'
+  }
+}
+
+function toggleWordSelection(word) {
+  const index = selectedWords.value.findIndex(w => w.id === word.id)
+  if (index > -1) {
+    selectedWords.value.splice(index, 1)
+  } else {
+    selectedWords.value.push(word)
+  }
+}
+
+async function deleteSelectedWords() {
+  if (selectedWords.value.length === 0) {
+    adminError.value = 'Keine Wörter ausgewählt'
+    return
+  }
+  
+  try {
+    for (const word of selectedWords.value) {
+      await deleteWordAPI(word.word)
+    }
+    adminMessage.value = `${selectedWords.value.length} Wörter wurden gelöscht`
+    selectedWords.value = []
+    await loadWords()
+  } catch (err) {
+    adminError.value = err.error || err.message || 'Fehler beim Löschen der Wörter'
+  }
+}

@@ .. @@
 function openAdminPanel() {
   activeScreen.value = 'admin'
   selectedUsers.value = []
+  selectedWords.value = []
   adminMessage.value = ''
   adminError.value = ''
   loadPendingUsers()
+  loadWords()
 }

@@ .. @@
     passwordMessage.value = 'Passwort erfolgreich geändert'
     oldPassword.value = ''
     newPassword.value = ''
     confirmPassword.value = ''
   } catch (err) {
-    passwordError.value = err.error || 'Fehler beim Ändern des Passworts'
+    passwordError.value = err.error || err.message || 'Fehler beim Ändern des Passworts'
   }
 }

@@ .. @@
   <!-- Admin-Bildschirm -->
   <section v-if="activeScreen === 'admin'" class="screen">
     <header>
       <button @click="activateSelectedUsers" :disabled="selectedUsers.length === 0">Freischalten</button>
       <button @click="deleteSelectedUsers" :disabled="selectedUsers.length === 0" class="delete-btn">Löschen</button>
+      <button @click="deleteSelectedWords" :disabled="selectedWords.length === 0" class="delete-btn">Wörter löschen</button>
     </header>
     
     <div v-if="adminMessage" class="admin-message success">{{ adminMessage }}</div>
@@ .. @@
       </div>
       <div class="box">
         <h3>WÖRTER BEARBEITEN</h3>
-        <textarea placeholder="Wortliste bearbeiten..."></textarea>
+        <div class="word-management">
+          <div class="add-word-section">
+            <input 
+              v-model="newWord" 
+              type="text" 
+              placeholder="Neues Wort hinzufügen..." 
+              @keyup.enter="addWord"
+              maxlength="10"
+            />
+            <button @click="addWord" :disabled="!newWord || newWord.length < 4">
+              Hinzufügen
+            </button>
+          </div>
+          
+          <div class="words-list">
+            <div v-if="words.length === 0" class="no-words">
+              Keine Wörter in der Datenbank
+            </div>
+            <div v-else class="words-grid">
+              <div 
+                v-for="word in words" 
+                :key="word.id" 
+                class="word-item"
+                :class="{ 'selected': selectedWords.some(w => w.id === word.id) }"
+                @click="toggleWordSelection(word)"
+              >
+                <input 
+                  type="checkbox" 
+                  :checked="selectedWords.some(w => w.id === word.id)"
+                  @click.stop
+                />
+                <span class="word-text">{{ word.word }}</span>
+              </div>
+            </div>
+          </div>
+        </div>
       </div>
     </main>
     <button class="close-btn" @click="activeScreen = 'game'">ADMIN BEREICH SCHLIESSEN</button>
@@ .. @@
 button:disabled {
   background-color: #6c757d !important;
   cursor: not-allowed !important;
   opacity: 0.6;
+}
+
+.word-management {
+  display: flex;
+  flex-direction: column;
+  gap: 15px;
+}
+
+.add-word-section {
+  display: flex;
+  gap: 10px;
+}
+
+.add-word-section input {
+  flex: 1;
+  padding: 8px 12px;
+  border: 1px solid #d3d6da;
+  border-radius: 4px;
+  font-size: 14px;
+}
+
+.add-word-section button {
+  padding: 8px 16px;
+  background-color: #28a745;
+  color: white;
+  border: none;
+  border-radius: 4px;
+  cursor: pointer;
+}
+
+.add-word-section button:hover:not(:disabled) {
+  background-color: #218838;
+}
+
+.words-list {
+  max-height: 300px;
+  overflow-y: auto;
+}
+
+.no-words {
+  text-align: center;
+  color: #6c757d;
+  font-style: italic;
+  padding: 20px;
+}
+
+.words-grid {
+  display: grid;
+  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
+  gap: 8px;
+}
+
+.word-item {
+  display: flex;
+  align-items: center;
+  padding: 6px 8px;
+  background: white;
+  border: 1px solid #d3d6da;
+  border-radius: 4px;
+  cursor: pointer;
+  transition: all 0.2s;
+  font-size: 13px;
+}
+
+.word-item:hover {
+  background-color: #f8f9fa;
+  border-color: #007bff;
+}
+
+.word-item.selected {
+  background-color: #e3f2fd;
+  border-color: #2196f3;
+}
+
+.word-item input[type="checkbox"] {
+  margin-right: 6px;
+  cursor: pointer;
+}
+
+.word-text {
+  font-weight: bold;
+  text-transform: uppercase;
 }