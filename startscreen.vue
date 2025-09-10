@@ .. @@
 <script setup>
 import { ref } from 'vue'
 import { loginAPI, registerAPI } from '../api.js'

 const emit = defineEmits(['login-successful'])

 const username = ref('')
 const password = ref('')
 const error = ref(null)
 const success = ref(null)
 const isLoading = ref(false)

 async function login() {
   isLoading.value = true
   error.value = null
   success.value = null
+  
+  if (!username.value || !password.value) {
+    error.value = 'Bitte Benutzername und Passwort eingeben'
+    isLoading.value = false
+    return
+  }
+  
   try {
     const userData = await loginAPI(username.value, password.value)
     emit('login-successful', userData)
   } catch (err) {
-    error.value = err.error || err
+    error.value = err.error || err.message || 'Login fehlgeschlagen'
   } finally {
     isLoading.value = false
   }
 }

 async function register() {
   if (!username.value || !password.value) {
     error.value = 'Bitte Benutzername und Passwort eingeben'
     return
   }
   
   isLoading.value = true
   error.value = null
   success.value = null
   try {
     const result = await registerAPI(username.value, password.value)
-    success.value = 'Registrierung erfolgreich! Warte auf Admin-Freischaltung.'
+    success.value = result.message || 'Registrierung erfolgreich! Warte auf Admin-Freischaltung.'
     username.value = ''
     password.value = ''
   } catch (err) {
-    error.value = err.error || err
+    error.value = err.error || err.message || 'Registrierung fehlgeschlagen'
   } finally {
     isLoading.value = false
   }
 }