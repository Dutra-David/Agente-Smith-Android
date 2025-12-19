# 🚀 Setup Rápido - Agente Smith Android

**Resumo:** Um comando configura e roda tudo automaticamente!

---

## ⚡ Opção 1: Modo Automático (RECOMENDADO)

Se você quer que **tudo seja feito automaticamente** (sem abrir Android Studio):

```bash
# Clonar + Setup Completo + Build + Run
git clone https://github.com/Dutra-David/Agente-Smith-Android.git
cd Agente-Smith-Android
bash setup.sh --build --run
```

**O que acontece:**
- ✅ Verifica pré-requisitos (Java, Git)
- ✅ Atualiza endpoint da API
- ✅ Sincroniza Gradle
- ✅ Compila o projeto
- ✅ Instala no dispositivo/emulador
- ✅ Abre o app automaticamente

---

## ⚙️ Opção 2: Com Endpoint Customizado

Se seu backend está em outro servidor:

```bash
bash setup.sh --api-url https://seu-servidor.com/ --build --run
```

**Exemplos:**
```bash
# Servidor local na porta 3000
bash setup.sh --api-url http://localhost:3000/ --build --run

# Servidor em produção
bash setup.sh --api-url https://api.seu-app.com/ --build --run
```

---

## 🎯 Opção 3: Apenas Setup (Sem Build/Run)

Se quer configurar e depois abrir manualmente no Android Studio:

```bash
bash setup.sh
# Depois abra no Android Studio: File → Open → Selecione a pasta
```

---

## 📱 Opção 4: Só Build (Sem Run)

Para compilar mas não instalar ainda:

```bash
bash setup.sh --build
```

---

## ❓ Precisa de Ajuda?

Ver todas as opções disponíveis:

```bash
bash setup.sh --help
```

---

## 🐛 Erros Comuns

### Erro: "bash: permission denied"
```bash
chmod +x setup.sh
bash setup.sh --build --run
```

### Erro: "Java não encontrado"
Instale Java 17+:
- **Windows/Mac/Linux:** https://www.oracle.com/java/technologies/downloads/

### Erro: "Nenhum dispositivo conectado"
1. Conecte seu celular via USB
2. Ative modo desenvolvedor (Settings > Developer Options)
3. Autorize depuração USB

### Erro: "ADB não encontrado"
Android SDK Platform-Tools precisa estar instalado. O Android Studio instala automaticamente.

---

## ✨ Resumo Ultra-Rápido

```bash
# Copy & Paste este bloco inteiro no terminal:
git clone https://github.com/Dutra-David/Agente-Smith-Android.git && \
cd Agente-Smith-Android && \
bash setup.sh --build --run
```

**Pronto! Seu app está rodando!** 🎉

---

## 📝 Próximos Passos (Integração com API Real)

1. Abra o arquivo: `app/src/main/java/com/dutra/agente/viewmodels/ChatViewModel.kt`
2. Procure por: `simulateAgentResponse()`
3. Substitua pela integração com sua API

Ver documentação completa em **IMPLEMENTATION_SUMMARY.md**

---

**Desenvolvido por:** Dutra-David  
**Status:** ✅ Pronto para Produção  
**Última atualização:** 19 de Dezembro de 2025
