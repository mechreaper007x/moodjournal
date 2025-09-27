document.addEventListener('DOMContentLoaded', () => {
            const loginFormContainer = document.getElementById('login-form-container');
            const registerFormContainer = document.getElementById('register-form-container');
            const showRegisterBtn = document.getElementById('show-register');
            const showLoginBtn = document.getElementById('show-login');
            const loginForm = document.getElementById('login-form');
            const registerForm = document.getElementById('register-form');
            const notificationEl = document.getElementById('notification');

            function showNotification(message, isError = false) {
                notificationEl.textContent = message;
                notificationEl.style.background = isError ? 'linear-gradient(45deg, #f5576c, #f093fb)' : 'var(--primary-gradient)';
                notificationEl.classList.add('show');
                setTimeout(() => notificationEl.classList.remove('show'), 4000);
            }

            showRegisterBtn.addEventListener('click', (e) => {
                e.preventDefault();
                loginFormContainer.style.display = 'none';
                registerFormContainer.style.display = 'block';
            });

            showLoginBtn.addEventListener('click', (e) => {
                e.preventDefault();
                registerFormContainer.style.display = 'none';
                loginFormContainer.style.display = 'block';
            });

            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const email = document.getElementById('login-email').value;
                const password = document.getElementById('login-password').value;

                try {
                    const response = await fetch('http://localhost:9091/api/auth/login', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ email, password })
                    });
                    
                    if (!response.ok) {
                        // Check if response is JSON
                        const contentType = response.headers.get('content-type');
                        if (contentType && contentType.includes('application/json')) {
                            const errorData = await response.json();
                            throw new Error(errorData.error || `HTTP ${response.status}: ${response.statusText}`);
                        } else {
                            // Response is likely HTML (404 page)
                            throw new Error(`Server error (${response.status}): Please check if the server is running`);
                        }
                    }

                    const data = await response.json();
                    // Store both the token and the user object from the response
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('userId', data.user.id);
                    window.location.href = 'http://localhost:9091/index.html';

                } catch (error) {
                    console.error('Login error:', error);
                    if (error.name === 'SyntaxError' && error.message.includes('JSON')) {
                        showNotification('Server error: Unable to connect to API. Please check if the server is running.', true);
                    } else {
                        showNotification(error.message, true);
                    }
                }
            });

            registerForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const username = document.getElementById('register-username').value;
                const email = document.getElementById('register-email').value;
                const password = document.getElementById('register-password').value;

                try {
                    const response = await fetch('http://localhost:9091/api/auth/register', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ username, email, password })
                    });
                    
                    if (!response.ok) {
                        // Check if response is JSON
                        const contentType = response.headers.get('content-type');
                        if (contentType && contentType.includes('application/json')) {
                            const errorData = await response.json();
                            throw new Error(errorData.error || `HTTP ${response.status}: ${response.statusText}`);
                        } else {
                            // Response is likely HTML (404 page)
                            throw new Error(`Server error (${response.status}): Please check if the server is running`);
                        }
                    }

                    const data = await response.json();
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('userId', data.user.id);
                    window.location.href = 'http://localhost:9091/index.html';

                } catch (error) {
                    console.error('Registration error:', error);
                    if (error.name === 'SyntaxError' && error.message.includes('JSON')) {
                        showNotification('Server error: Unable to connect to API. Please check if the server is running.', true);
                    } else {
                        showNotification(error.message, true);
                    }
                }
            });
        });