document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus();
    loadJournalEntries();

    const journalForm = document.getElementById('journalForm');
    const entryContent = document.getElementById('entryContent');
    const moodSuggestion = document.getElementById('moodSuggestion');

    // Handle form submission for new/updated entries
    journalForm.addEventListener('submit', function(event) {
        event.preventDefault();
        saveJournalEntry();
    });

    // AI Mood Suggestion Listener
    entryContent.addEventListener('input', function() {
        suggestMood(this.value);
    });

    // Make the mood suggestion clickable
    moodSuggestion.addEventListener('click', function() {
        const suggestedMood = document.getElementById('suggestedMood').textContent.toUpperCase();
        if (suggestedMood) {
            document.getElementById('entryMood').value = suggestedMood;
            this.style.display = 'none'; // Hide after selection
        }
    });

    // Logout functionality
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', logout);
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
        return;
    }

    fetch(`/api/journal-entries/user/${userId}`)
        .then(response => response.json())
        .then(entries => {
            const entriesList = document.getElementById('entriesList');
            entriesList.innerHTML = ''; // Clear current list
            entries.forEach(entry => {
                const entryElement = document.createElement('div');
                entryElement.className = 'entry';
                entryElement.innerHTML = `
                    <h3>${entry.title}</h3>
                    <p class="entry-meta"><strong>Mood:</strong> ${entry.mood} | <strong>Visibility:</strong> ${entry.visibility}</p>
                    <p>${entry.content.substring(0, 150)}...</p>
                    <button onclick="editEntry(${entry.id})">Edit</button>
                    <button onclick="deleteEntry(${entry.id})">Delete</button>
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
            body: JSON.stringify({ content })
        })
        .then(response => response.json())
        .then(data => {
            if (data.mood && data.mood !== 'NEUTRAL') {
                document.getElementById('suggestedMood').textContent = data.mood.toLowerCase();
                moodSuggestionContainer.style.display = 'block';
            } else {
                moodSuggestionContainer.style.display = 'none';
            }
        })
        .catch(error => console.error('Error getting mood suggestion:', error));
}

// Function to clear the form fields
function resetForm() {
    document.getElementById('journalForm').reset();
    document.getElementById('entryId').value = '';
    document.getElementById('formTitle').textContent = 'New Journal Entry';
    document.getElementById('moodSuggestion').style.display = 'none';
}