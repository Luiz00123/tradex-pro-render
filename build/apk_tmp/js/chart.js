// TradeX Pro - Chart Engine
console.log("🦇 Chart Engine initialized");

function initChart() {
    const canvas = document.getElementById('hero-chart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    canvas.width = 400;
    canvas.height = 200;
    
    let data = [];
    let price = 67245;
    
    // Generate initial data
    for (let i = 0; i < 50; i++) {
        price += (Math.random() - 0.5) * 100;
        data.push(price);
    }
    
    function drawChart() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        
        // Draw grid
        ctx.strokeStyle = 'rgba(255,255,255,0.05)';
        ctx.lineWidth = 1;
        for (let i = 0; i < 5; i++) {
            const y = (canvas.height / 5) * i;
            ctx.beginPath();
            ctx.moveTo(0, y);
            ctx.lineTo(canvas.width, y);
            ctx.stroke();
        }
        
        // Draw line
        ctx.strokeStyle = '#00d4ff';
        ctx.lineWidth = 2;
        ctx.beginPath();
        
        const step = canvas.width / (data.length - 1);
        data.forEach((point, i) => {
            const x = i * step;
            const y = canvas.height - ((point - Math.min(...data)) / (Math.max(...data) - Math.min(...data))) * canvas.height;
            
            if (i === 0) {
                ctx.moveTo(x, y);
            } else {
                ctx.lineTo(x, y);
            }
        });
        
        ctx.stroke();
        
        // Add gradient fill
        const gradient = ctx.createLinearGradient(0, 0, 0, canvas.height);
        gradient.addColorStop(0, 'rgba(0,212,255,0.3)');
        gradient.addColorStop(1, 'rgba(0,212,255,0)');
        
        ctx.fillStyle = gradient;
        ctx.beginPath();
        ctx.moveTo(0, canvas.height);
        data.forEach((point, i) => {
            const x = i * step;
            const y = canvas.height - ((point - Math.min(...data)) / (Math.max(...data) - Math.min(...data))) * canvas.height;
            ctx.lineTo(x, y);
        });
        ctx.lineTo(canvas.width, canvas.height);
        ctx.fill();
    }
    
    // Update data
    setInterval(() => {
        const lastPrice = data[data.length - 1];
        const newPrice = lastPrice + (Math.random() - 0.5) * 100;
        data.push(newPrice);
        if (data.length > 50) data.shift();
        drawChart();
    }, 1000);
    
    drawChart();
}

// Initialize on load
document.addEventListener('DOMContentLoaded', initChart);
