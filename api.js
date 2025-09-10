const API_BASE_URL = 'http://localhost:8080/api';

// Hilfsfunktion für API-Aufrufe OHNE Basic Auth
async function apiCall(endpoint, method = 'GET', data = null) {
  const config = {
    method,
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json'
    },
    credentials: 'include'
  };

  if (data && method !== 'GET') {
    config.body = new URLSearchParams(data);
  }

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    return { data: result };
  } catch (error) {
    console.error('API call failed:', error);
    return { error: error.message };
  }
}

export async function registerAPI(username, password) {
    try {
        const result = await apiCall('/user/register', 'POST', { username, password });
       
        if (result.data && result.data.success) {
            return {
                success: true,
                data: result.data
            };
        } else {
            return {
                success: false,
                error: result.data?.message || 'Registration failed'
            };
        }
    } catch (error) {
        return {
            success: false,
            error: error.message
        };
    }
}

export async function loginAPI(username, password) {
    try {
        const result = await apiCall('/user/login', 'POST', { username, password });
       
        if (result.data && result.data.success) {
            return {
                success: true,
                data: result.data
            };
        } else {
            return {
                success: false,
                error: result.data?.message || 'Login failed'
            };
        }
    } catch (error) {
        return {
            success: false,
            error: error.message
        };
    }
}

export async function getUserAPI(userID) {
    try {
        const result = await apiCall(`/user?username=${userID}`, 'GET');
        return {
            success: true,
            data: result.data
        };
    } catch (error) {
        return {
            success: false,
            error: error.message
        };
    }
}

export async function saveScoreAPI(username, score) {
    try {
        const result = await apiCall('/highscores/save', 'POST', { username, score });
       
        if (result.data && result.data.success !== false) {
            return {
                success: true,
                data: result.data
            };
        } else {
            return {
                success: false,
                error: result.data?.message || 'Score save failed'
            };
        }
    } catch (error) {
        return {
            success: false,
            error: error.message
        };
    }
}

export async function getHighscoresAPI() {
    try {
        const result = await apiCall('/highscores', 'GET');
        return {
            success: true,
            data: result.data
        };
    } catch (error) {
        return {
            success: false,
            error: error.message
        };
    }
}