#!/bin/bash

# Agente Smith Android - Setup Automation Script
# Script para automatizar todo o processo de setup, build e deploy
# Uso: bash setup.sh [--api-url YOUR_API_URL] [--build] [--run]

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações padrão
API_URL="http://localhost:8000/"
BUILD_PROJECT=false
RUN_PROJECT=false
PROJECT_DIR="."

# Função para imprimir com cores
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[⚠]${NC} $1"
}

# Parse argumentos
while [[ $# -gt 0 ]]; do
    case $1 in
        --api-url)
            API_URL="$2"
            shift 2
            ;;
        --build)
            BUILD_PROJECT=true
            shift
            ;;
        --run)
            RUN_PROJECT=true
            shift
            ;;
        --help)
            echo "Uso: bash setup.sh [opções]"
            echo "Opções:"
            echo "  --api-url URL      Define URL da API (padrão: http://localhost:8000/)"
            echo "  --build            Compila o projeto automaticamente"
            echo "  --run              Executa o app após build"
            echo "  --help             Mostra esta ajuda"
            echo ""
            echo "Exemplos:"
            echo "  bash setup.sh"
            echo "  bash setup.sh --api-url https://api.example.com/"
            echo "  bash setup.sh --api-url https://api.example.com/ --build --run"
            exit 0
            ;;
        *)
            print_error "Argumento desconhecido: $1"
            exit 1
            ;;
    esac
done

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║${NC}     Agente Smith Android - Setup Automation Script     ${BLUE}║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# ============================================================================
# 1. VERIFICAÇÕES PRÉ-REQUISITOS
# ============================================================================
print_status "Verificando pré-requisitos..."

# Verificar Git
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version | awk '{print $3}')
    print_success "Git encontrado (v$GIT_VERSION)"
else
    print_error "Git não encontrado. Instale Git primeiro."
    exit 1
fi

# Verificar Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | grep -oP '(\d+)(\.\d+)?' | head -1)
    print_success "Java encontrado (v$JAVA_VERSION)"
else
    print_error "Java não encontrado. Instale Java 17+"
    exit 1
fi

# Verificar se é repositório Git
if [ -d ".git" ]; then
    print_success "Repositório Git detectado"
else
    print_warning "Não é um repositório Git. Pulando atualização."
fi

echo ""

# ============================================================================
# 2. ATUALIZAR ENDPOINT DA API
# ============================================================================
print_status "Configurando endpoint da API para: $API_URL"

RETROFIT_FILE="app/src/main/java/com/dutra/agente/network/RetrofitClient.kt"

if [ -f "$RETROFIT_FILE" ]; then
    # Backup do arquivo original
    cp "$RETROFIT_FILE" "${RETROFIT_FILE}.bak"
    print_success "Backup criado: ${RETROFIT_FILE}.bak"
    
    # Substituir URL
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        sed -i '' "s|private const val BASE_URL = .*|private const val BASE_URL = \"$API_URL\"|" "$RETROFIT_FILE"
    else
        # Linux
        sed -i "s|private const val BASE_URL = .*|private const val BASE_URL = \"$API_URL\"|" "$RETROFIT_FILE"
    fi
    
    print_success "Endpoint atualizado: $API_URL"
else
    print_error "Arquivo RetrofitClient.kt não encontrado em: $RETROFIT_FILE"
    exit 1
fi

echo ""

# ============================================================================
# 3. SINCRONIZAR GRADLE
# ============================================================================
print_status "Sincronizando Gradle..."

if [ -f "gradlew" ]; then
    chmod +x gradlew
    ./gradlew --version
    print_success "Gradle wrapper encontrado"
else
    print_warning "Gradle wrapper não encontrado. Usando gradle do sistema."
fi

echo ""

# ============================================================================
# 4. COMPILAR PROJETO (OPCIONAL)
# ============================================================================
if [ "$BUILD_PROJECT" = true ]; then
    print_status "Compilando projeto..."
    
    if [ -f "gradlew" ]; then
        ./gradlew build
        if [ $? -eq 0 ]; then
            print_success "Build concluído com sucesso!"
        else
            print_error "Build falhou. Verifique os erros acima."
            exit 1
        fi
    else
        print_error "Gradle wrapper não encontrado. Execute build manualmente."
    fi
    echo ""
fi

# ============================================================================
# 5. EXECUTAR PROJETO (OPCIONAL)
# ============================================================================
if [ "$RUN_PROJECT" = true ]; then
    print_status "Preparando para executar o app..."
    
    # Verificar dispositivos conectados
    if command -v adb &> /dev/null; then
        DEVICES=$(adb devices | grep -v "List" | grep -v "^$")
        
        if [ -z "$DEVICES" ]; then
            print_error "Nenhum dispositivo/emulador conectado."
            print_status "Conecte um dispositivo via USB ou inicie um emulador."
            echo ""
            echo "Dica: Se tiver um dispositivo conectado, autorize depuração USB no celular."
        else
            print_success "Dispositivo(s) encontrado(s):"
            echo "$DEVICES"
            echo ""
            
            if [ -f "gradlew" ]; then
                print_status "Executando: gradlew installDebug"
                ./gradlew installDebug
                
                if [ $? -eq 0 ]; then
                    print_success "App instalado com sucesso!"
                    
                    # Iniciar a activity
                    print_status "Iniciando app..."
                    adb shell am start -n com.dutra.agente/.MainActivity
                    print_success "App iniciado!"
                else
                    print_error "Erro ao instalar app."
                fi
            fi
        fi
    else
        print_error "ADB não encontrado. Instale Android SDK Platform Tools."
    fi
    echo ""
fi

# ============================================================================
# 6. RESUMO FINAL
# ============================================================================
echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║${NC}              Setup Concluído com Sucesso!              ${GREEN}║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
print_success "API URL configurada: $API_URL"
echo ""
echo "Próximos passos:"
echo ""
echo "1. Se ainda não abriu o Android Studio:"
echo "   ${BLUE}File → Open → Selecione a pasta do projeto${NC}"
echo ""
echo "2. Aguarde a sincronização do Gradle"
echo ""
echo "3. Conecte um dispositivo/emulador"
echo ""
echo "4. Clique no botão ▶️ (Run) ou execute:"
echo "   ${BLUE}bash setup.sh --build --run${NC}"
echo ""
echo "5. Integrar com API real:"
echo "   - Editar: app/src/main/java/com/dutra/agente/viewmodels/ChatViewModel.kt"
echo "   - Substituir simulateAgentResponse() pela integração com MessageRepository"
echo ""
echo -e "${YELLOW}Documentação: Ver IMPLEMENTATION_SUMMARY.md${NC}"
echo ""
