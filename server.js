@@ .. @@
 const WebSocket = require('ws');
 const http = require('http');
 const axios = require('axios'); 

 // --- Server-Konfiguration ---
 const PORT = 3000;
-const BACKEND_URL = 'http://localhost:8080'; 
+const BACKEND_URL = 'http://localhost:8080/api'; 
 const ROUND_DURATION = 60;

@@ .. @@
   // Zufälliges Wort vom Backend holen
   async fetchRandomWord() {
     try {
       console.log('🔄 Zufälliges Wort vom Backend abrufen...');
       const response = await axios.get(`${this.backendUrl}/words/randomWord`, {
-        auth: {
-          username: 'user',
-          password: 'passwordtest'
-        }
+        headers: {
+          'Content-Type': 'application/json'
+        }
       });
       
-      const word = response.data.toUpperCase();
+      const word = response.data.word ? response.data.word.toUpperCase() : response.data.toUpperCase();
       console.log(`✅ Wort vom Backend erhalten: ${word}`);
       return word;
       
     } catch (error) {
       console.error('❌ Fehler bei Backend-API:', error.message);
       
       // Fallback zu hardcoded Liste
       console.log('🔄 Verwende Fallback-Wortliste...');
       const fallbackWords = ['HOUSE', 'MAGIC', 'PHONE', 'WORLD', 'BREAD', 'MUSIC', 'LIGHT'];
       const word = fallbackWords[Math.floor(Math.random() * fallbackWords.length)];
       console.log(`⚠️ Fallback-Wort wird genutzt: ${word}`);
       return word;
     }
   }

@@ .. @@
   // Punkte an Backend senden
   async savePlayerScore(username, score) {
     try {
       const response = await axios.post(`${this.backendUrl}/highscores/save`, 
-        { username, score },
+        new URLSearchParams({ username, score }),
         {
-          auth: {
-            username: 'user',
-            password: 'passwordtest'
+          headers: {
+            'Content-Type': 'application/x-www-form-urlencoded'
           }
         }
       );
       console.log(`✅ Punkte für ${username} gespeichert: ${score}`);
     } catch (error) {
       console.error(`❌ Fehler beim Speichern der Punkte für ${username}:`, error.message);
     }
   }

@@ .. @@
   // Backend-Verbindung testen
   async testBackendConnection() {
     try {
       const response = await axios.get(`${this.backendUrl}/words`, {
-        auth: {
-          username: 'user',
-          password: 'passwordtest'
+        headers: {
+          'Content-Type': 'application/json'
         }
       });
       console.log(`✅ Backend-Verbindung erfolgreich! ${response.data.length} Wörter in der Datenbank gefunden.`);
       return true;
     } catch (error) {
       console.error(`❌ Backend-Verbindung fehlgeschlagen:`, error.message);
       return false;
     }
   }