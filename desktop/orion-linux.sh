#!/usr/bin/env bash
# 🌌 Orion Browser - Linux Desktop Launcher
# Architect & Developer: Abhinav Santhosh
# Copyright (c) 2026 Abhinav Santhosh. All Rights Reserved.

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

echo "🌌 Launching Orion Browser (Linux Desktop)..."
echo "Architected & Built by Abhinav Santhosh"

if ! command -v node &> /dev/null; then
    echo "Error: Node.js is required to run Orion Desktop. Please install Node.js."
    exit 1
fi

if [ ! -d "node_modules" ]; then
    echo "Installing desktop dependencies..."
    npm install
fi

# Launch Electron browser
npx electron . "$@"
