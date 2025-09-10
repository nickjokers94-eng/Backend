@@ .. @@
-const API_BASE_URL = 'http://localhost:8080';
+const API_BASE_URL = 'http://localhost:8080/api';

@@ .. @@
 // Hilfsfunktion für API-Aufrufe OHNE Basic Auth
 async function apiCall(endpoint, method = 'GET', data = null) {
   const config = {
     method,
     headers: {
-        'Content-Type': 'application/x-www-form-urlencoded'
+        'Content-Type': 'application/x-www-form-urlencoded',
+        'Accept': 'application/json'
     },
     credentials: 'include'
   };
@@ .. @@
     try {
-        const result = await apiCall('/user/register', 'POST', { username, password });
+        const result = await apiCall('/user/register', 'POST', { username, password });
        
         if (result.data && result.data.success) {
             return {
@@ .. @@
     try {
-        const result = await apiCall('/user/login', 'POST', { username, password });
+        const result = await apiCall('/user/login', 'POST', { username, password });
        
         if (result.data && result.data.success) {
             return {
@@ .. @@
 export async function getUserAPI(userID) {
     try {
-        const result = await apiCall(`/user?userID=${userID}`, 'GET');
+        const result = await apiCall(`/user?username=${userID}`, 'GET');
         return {
             success: true,
             data: result.data
@@ .. @@
 export async function saveScoreAPI(username, score) {
     try {
-        const result = await apiCall('/user/saveScore', 'POST', { username, score });
+        const result = await apiCall('/highscores/save', 'POST', { username, score });
        
         if (result.data && result.data.success !== false) {
             return {