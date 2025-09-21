document.addEventListener('DOMContentLoaded', function() {
    // This function runs once the HTML document is fully loaded.

    // Universal functions for all pages
    checkLoginStatus();
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', logout);
    }

    // --- Page-Specific Logic for the Main Journal Page ---

    const journalForm = document.getElementById('journalForm');
    const entryContent = document.getElementById('entryContent');
    const moodSuggestion = document.getElementById('moodSuggestion');

    // Only run journal-related code if the main form exists on the page.
    if (journalForm) {
        loadJournalEntries();

        journalForm.addEventListener('submit', function(event) {
            event.preventDefault();
            saveJournalEntry();
        });
    }

    // Only add the AI suggestion listener if the content box exists.
    if (entryContent) {
        entryContent.addEventListener('input', function() {
            suggestMood(this.value);
        });
    }

    // Only add the click listener if the suggestion element exists.
    if (moodSuggestion) {
        moodSuggestion.addEventListener('click', function() {
            const suggestedMoodText = document.getElementById('suggestedMood').textContent;
            // Correctly extracts the mood (e.g., "happy" from "happy (95.24%)")
            const mood = suggestedMoodText.split(' ')[0].toUpperCase();
            
            if (mood) {
                document.getElementById('entryMood').value = mood;
                this.style.display = 'none'; // Hide after selection
            }
        });
    }
});

// Function to save or update a journal entry
function saveJournalEntry() {
    const entryId = document.getElementById('entryId').value;
    const title = document.getElementById('entryTitle').value;
    const content = document.getElementById('entryContent').value;
    const mood = document.getElementById('entryMood').value;
    const visibility = document.getElementById('entryVisibility').value;
    const userId = localStorage.getItem('userId');

    if (!userId) {
        alert('You must be logged in to save an entry.');
        return;
    }

    const entryData = {
        title,
        content,
        mood,
        visibility,
        userId: parseInt(userId, 10)
    };

    const method = entryId ? 'PUT' : 'POST';
    const url = entryId ? `/api/journal-entries/${entryId}` : '/api/journal-entries';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(entryData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to save entry.');
        }
        return response.json();
    })
    .then(() => {
        resetForm();
        loadJournalEntries();
    })
    .catch(error => console.error('Error saving entry:', error));
}

// Function to load and display all journal entries for the logged-in user
function loadJournalEntries() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        console.log('No user ID found, cannot load entries.');
        return;
    }

    fetch(`/api/journal-entries/user/${userId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(entries => {
            const entriesList = document.getElementById('entriesList');
            if (!entriesList) return;
            
            entriesList.innerHTML = ''; // Clear current list
            entries.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)); // Sort by most recent
            
            entries.forEach(entry => {
                const entryElement = document.createElement('div');
                entryElement.className = 'entry';
                entryElement.innerHTML = `
                    <h3>${escapeHTML(entry.title)}</h3>
                    <p class="entry-meta">
                        <strong>Mood:</strong> ${escapeHTML(entry.mood)} | 
                        <strong>Date:</strong> ${new Date(entry.createdAt).toLocaleDateString()}
                    </p>
                    <p>${escapeHTML(entry.content.substring(0, 150))}...</p>
                    <div class="entry-actions">
                        <button onclick="editEntry(${entry.id})">Edit</button>
                        <button onclick="deleteEntry(${entry.id})">Delete</button>
                    </div>
                `;
                entriesList.appendChild(entryElement);
            });
        })
        .catch(error => console.error('Error loading entries:', error));
}

// Function to populate the form for editing an entry
function editEntry(id) {
    fetch(`/api/journal-entries/${id}`)
        .then(response => response.json())
        .then(entry => {
            document.getElementById('entryId').value = entry.id;
            document.getElementById('entryTitle').value = entry.title;
            document.getElementById('entryContent').value = entry.content;
            document.getElementById('entryMood').value = entry.mood;
            document.getElementById('entryVisibility').value = entry.visibility;
            document.getElementById('formTitle').textContent = 'Edit Journal Entry';
            window.scrollTo(0, 0); // Scroll to top to see the form
        });
}

// Function to delete an entry
function deleteEntry(id) {
    if (confirm('Are you sure you want to delete this entry?')) {
        fetch(`/api/journal-entries/${id}`, {
                method: 'DELETE'
            })
            .then(response => {
                if (response.ok) {
                    loadJournalEntries();
                } else {
                    alert('Failed to delete entry.');
                }
            });
    }
}

// Function to get mood suggestion from the AI service
function suggestMood(content) {
    const moodSuggestionContainer = document.getElementById('moodSuggestion');
    if (content.trim().length < 15) { // Only suggest after some typing
        moodSuggestionContainer.style.display = 'none';
        return;
    }

    fetch('/api/ai/suggest-mood', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const suggestedMoodEl = document.getElementById('suggestedMood');
            const suggestedSignsEl = document.getElementById('suggestedSigns');

            if (data.mood && data.mood !== 'NEUTRAL') {
                // Display mood and confidence
                suggestedMoodEl.textContent = `${data.mood.toLowerCase()} (${data.confidence}%)`;

                // Display other detected signs if they exist
                if (data.signs && data.signs.length > 0) {
                    suggestedSignsEl.textContent = `| Signs: ${data.signs.join(', ')}`;
                } else {
                    suggestedSignsEl.textContent = '';
                }
                moodSuggestionContainer.style.display = 'block';
            } else {
                moodSuggestionContainer.style.display = 'none';
            }
        })
        .catch(error => console.error('Error getting mood suggestion:', error));
}

// Function to clear the form fields
function resetForm() {
    const form = document.getElementById('journalForm');
    if (form) {
        form.reset();
        document.getElementById('entryId').value = '';
        document.getElementById('formTitle').textContent = 'New Journal Entry';
        document.getElementById('moodSuggestion').style.display = 'none';
    }
}

// Utility to prevent XSS attacks
function escapeHTML(str) {
    return str.replace(/[&<>"']/g, function(match) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        }[match];
    });
}