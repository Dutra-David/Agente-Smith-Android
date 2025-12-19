#!/bin/bash

# Agente Smith Android - Open in Android Studio
# Script para abrir projeto automaticamente no Android Studio
# Funciona em: Windows (Git Bash/WSL), macOS, Linux

# Cores
BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║${NC}   Abrindo Agente Smith Android no Android Studio   ${BLUE}║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Detectar sistema operacional
OS="$(uname -s)"
PROJECT_PATH="$(pwd)"

echo -e "${GREEN}[✓]${NC} Sistema Operacional: $OS"
echo -e "${GREEN}[✓]${NC} Caminho do Projeto: $PROJECT_PATH"
echo ""

# Verificar se é realmente o projeto
if [ ! -f "build.gradle.kts" ] || [ ! -d "app" ]; then
    echo -e "${RED}[✗]${NC} Este não parece ser o diretório raiz do projeto."
    echo "    Execute este script na pasta raiz onde está 'build.gradle.kts'"
    exit 1
fi

echo -e "${GREEN}[✓]${NC} Projeto verificado com sucesso"
echo ""

# Tentar encontrar Android Studio
echo -e "${BLUE}[INFO]${NC} Procurando por Android Studio..."

if [[ "$OS" == "Darwin" ]]; then
    # macOS
    echo -e "${BLUE}[INFO]${NC} Detectado macOS"
    
    if [ -d "/Applications/Android Studio.app" ]; then
        echo -e "${GREEN}[✓]${NC} Android Studio encontrado em /Applications"
        echo -e "${YELLOW}[⚠]${NC} Abrindo projeto..."
        open -a "Android Studio" "$PROJECT_PATH"
        echo -e "${GREEN}[✓]${NC} Android Studio aberto com sucesso!"
    else
        echo -e "${YELLOW}[⚠]${NC} Android Studio não encontrado no local padrão."
        echo -e "${YELLOW}[⚠]${NC} Procurando em locais alternativos..."
        
        if command -v studio &> /dev/null; then
            echo -e "${GREEN}[✓]${NC} Encontrado via CLI"
            studio "$PROJECT_PATH" &
            echo -e "${GREEN}[✓]${NC} Android Studio aberto com sucesso!"
        else
            echo -e "${RED}[✗]${NC} Android Studio não encontrado."
            echo "    Instale Android Studio em /Applications/Android Studio.app"
            echo "    ou adicione ao PATH"
            exit 1
        fi
    fi

elif [[ "$OS" == "MINGW64_NT"* ]] || [[ "$OS" == "MSYS_NT"* ]]; then
    # Windows (Git Bash / WSL)
    echo -e "${BLUE}[INFO]${NC} Detectado Windows"
    
    # Procurar em locais comuns
    STUDIO_PATHS=(
        "C:\\Program Files\\Android\\Android Studio\\bin\\studio64.exe"
        "C:\\Program Files (x86)\\Android\\Android Studio\\bin\\studio.exe"
        "$LOCALAPPDATA\\Android\\Android Studio\\bin\\studio64.exe"
    )
    
    FOUND=false
    for path in "${STUDIO_PATHS[@]}"; do
        if [ -f "$path" ]; then
            echo -e "${GREEN}[✓]${NC} Android Studio encontrado em: $path"
            echo -e "${YELLOW}[⚠]${NC} Abrindo projeto..."
            "$path" "$PROJECT_PATH" &
            FOUND=true
            break
        fi
    done
    
    if [ "$FOUND" = false ]; then
        echo -e "${RED}[✗]${NC} Android Studio não encontrado nos locais padrão."
        echo ""
        echo "Locais procurados:"
        for path in "${STUDIO_PATHS[@]}"; do
            echo "  - $path"
        done
        echo ""
        echo "Solução:"
        echo "1. Instale Android Studio de https://developer.android.com/studio"
        echo "2. Ou adicione Android Studio ao PATH do Windows"
        exit 1
    else
        echo -e "${GREEN}[✓]${NC} Android Studio aberto com sucesso!"
    fi

else
    # Linux
    echo -e "${BLUE}[INFO]${NC} Detectado Linux"
    
    if command -v studio &> /dev/null; then
        echo -e "${GREEN}[✓]${NC} Android Studio encontrado via comando 'studio'"
        echo -e "${YELLOW}[⚠]${NC} Abrindo projeto..."
        studio "$PROJECT_PATH" &
        echo -e "${GREEN}[✓]${NC} Android Studio aberto com sucesso!"
    elif [ -f "$HOME/Android/studio/bin/studio.sh" ]; then
        echo -e "${GREEN}[✓]${NC} Android Studio encontrado no diretório padrão"
        echo -e "${YELLOW}[⚠]${NC} Abrindo projeto..."
        "$HOME/Android/studio/bin/studio.sh" "$PROJECT_PATH" &
        echo -e "${GREEN}[✓]${NC} Android Studio aberto com sucesso!"
    else
        echo -e "${RED}[✗]${NC} Android Studio não encontrado."
        echo ""
        echo "Solução:"
        echo "1. Instale Android Studio: https://developer.android.com/studio"
        echo "2. Ou adicione ao PATH:"
        echo "   export PATH=\"\$PATH:\$HOME/Android/studio/bin\""
        exit 1
    fi
fi

echo ""
echo -e "${GREEN}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║${NC}              Pronto! Android Studio Aberto!              ${GREEN}║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════╝${NC}"
echo ""
echo "Próximos passos:"
echo "1. Aguarde a sincronização do Gradle (pode levar 1-5 minutos)"
echo "2. Conecte um dispositivo/emulador via USB"
echo "3. Clique no botão ▶️ (Run) ou pressione Shift+F10"
echo ""
echo "Ou execute automaticamente:"
echo -e "   ${BLUE}bash setup.sh --build --run${NC}"
echo ""
