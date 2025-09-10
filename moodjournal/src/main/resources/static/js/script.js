/**
 * Draws a bar chart of mood frequencies on an HTML canvas.
 * @param {Array} entries - The list of journal entries.
 */
const drawMoodChart = (entries) => {
    const canvas = document.getElementById('mood-chart');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    const moodColors = {
        HAPPY: '#FFD700',   // Gold
        SAD: '#4169E1',     // Royal Blue
        ANXIOUS: '#FFA500', // Orange
        ANGRY: '#DC143C',   // Crimson
        CHILL: '#87CEEB',   // Sky Blue
        CALM: '#98FB98',    // Pale Green
        NEUTRAL: '#D3D3D3'  // Light Grey
    };
    const moods = Object.keys(moodColors);

    // 1. Count mood occurrences
    const moodCounts = moods.reduce((acc, mood) => ({ ...acc, [mood]: 0 }), {});
    entries.forEach(entry => {
        if (moodCounts.hasOwnProperty(entry.mood)) {
            moodCounts[entry.mood]++;
        }
    });

    // 2. Set up chart dimensions
    const chartWidth = canvas.width - 60;
    const chartHeight = canvas.height - 60;
    const barWidth = (chartWidth / moods.length) * 0.8;
    const barSpacing = (chartWidth / moods.length) * 0.2;
    const maxCount = Math.max(...Object.values(moodCounts), 1);

    // 3. Clear canvas and draw
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.font = '12px Arial';
    ctx.textAlign = 'center';

    let currentX = 40;

    moods.forEach(mood => {
        const count = moodCounts[mood];
        const barHeight = (count / maxCount) * chartHeight;
        const barTopY = chartHeight + 20 - barHeight;

        // Draw Bar
        ctx.fillStyle = moodColors[mood];
        ctx.fillRect(currentX, barTopY, barWidth, barHeight);

        // Draw Mood Label (X-axis)
        ctx.fillStyle = '#333';
        ctx.fillText(mood, currentX + barWidth / 2, chartHeight + 40);

        // --- THIS IS THE FIX ---
        // By setting the textBaseline to 'bottom', the 'y' coordinate in fillText
        // now refers to the bottom of the text, making positioning predictable.
        if (count > 0) {
            ctx.textBaseline = 'bottom'; // Set text alignment
            ctx.fillStyle = '#333';
            ctx.fillText(count, currentX + barWidth / 2, barTopY - 5); // Position 5px above the bar
        }
       
        currentX += barWidth + barSpacing;
    });

    // Draw Y-Axis line
    ctx.beginPath();
    ctx.moveTo(30, 20);
    ctx.lineTo(30, chartHeight + 20);
    ctx.strokeStyle = '#333';
    ctx.stroke();
};