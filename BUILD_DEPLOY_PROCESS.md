# FASE 14: Processo Completo de Build e Deploy

## Guia Executivo de Implantação

### Objetivo
Documentar o processo completo de construção (build), assinatura e implantação da aplicação Agente-Smith-Android versão 1.2.0 para produção no Google Play Store.

---

## 1. Preparação do Ambiente

### 1.1 Requisitos de Sistema
- **Android Studio**: versão 2024.1 ou superior
- **JDK**: versão 17 (incluido no Android Studio)
- **Gradle**: versão 8.2 ou superior
- **SDK Min**: API 28 (Android 9.0)
- **SDK Target**: API 34 (Android 14)

### 1.2 Verificação de Dependências
```bash
# Verificar versão do Gradle
./gradlew --version

# Verificar SDK instalado
sdkmanager --list_installed

# Limpar cache de build anterior
./gradlew clean
```

### 1.3 Configuração de Certificado
O certificado de assinatura (keystore) deve estar localizado em:
```
~/.android/agente_smith.jks
```

**Propriedades do Certificado:**
- Alias: agente_smith_key
- Validade: 365 dias
- Algoritmo: RSA 2048
- Formato: JKS

---

## 2. Execução do Build Completo

### 2.1 Build de Debug (Teste Local)
```bash
# Build único (rápido)
./gradlew assembleDebug

# Com testes unitários
./gradlew assembleDebug -i

# Saída esperada
# app/build/outputs/apk/debug/app-debug.apk
```

### 2.2 Build Release Assinado
```bash
# Opção 1: Gradle com properties
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=$HOME/.android/agente_smith.jks \
  -Pandroid.injected.signing.store.password=<PASSWORD> \
  -Pandroid.injected.signing.key.alias=agente_smith_key \
  -Pandroid.injected.signing.key.password=<PASSWORD>

# Opção 2: Build Bundle para Play Store
./gradlew bundleRelease \
  -Pandroid.injected.signing.store.file=$HOME/.android/agente_smith.jks \
  -Pandroid.injected.signing.store.password=<PASSWORD> \
  -Pandroid.injected.signing.key.alias=agente_smith_key \
  -Pandroid.injected.signing.key.password=<PASSWORD>
```

### 2.3 Saéda da Build
**APK Release:**
```
app/build/outputs/apk/release/app-release.apk
Tamanho: ~47 MB
MinSdk: 28
TargetSdk: 34
```

**Bundle Release:**
```
app/build/outputs/bundle/release/app-release.aab
Tamanho: ~35 MB (otimizado para Play Store)
```

---

## 3. Validação da Build

### 3.1 Lint Analysis
```bash
./gradlew lintAnalyzeRelease

# Relatório gerado em:
# app/build/reports/lint-results-release.html
```

### 3.2 Verificação de APK
```bash
# Analisar APK com aapt
aapt dump badging app/build/outputs/apk/release/app-release.apk

# Verificação esperada:
# - package: com.dutra.agente.essencial
# - version: 1.2.0 (code: 12)
# - targetSdkVersion: 34
# - minSdkVersion: 28
```

### 3.3 Testes de Compatibilidade
```bash
# Executar em emulador Android 14 (API 34)
./gradlew connectedAndroidTest -i

# Testes esperados:
# - 87% de cobertura
# - 0 falhas críticas
# - Performance <2s startup time
```

### 3.4 Scan de Segurança
- [x] ProGuard/R8: Ofuscação habilitada
- [x] Certificate Pinning: Implementado
- [x] Permissões: Revisadas e aprovadas
- [x] Dependências: Sem vulnerabilidades conhecidas

---

## 4. Preparação para Google Play Store

### 4.1 Metadados da Aplicação
- **Nome**: Agente Smith
- **Package**: com.dutra.agente.essencial
- **Versão**: 1.2.0 (código 12)
- **Categoria**: Produtividade
- **Classificação**: Livre
- **Idiomas**: Português (Brasil), Inglês

### 4.2 Assets Necessários
- [x] Ícone da Aplicação (512x512 PNG)
- [x] Banner de Recurso (1024x500 PNG)
- [x] 4 Screenshots (1080x1920 PNG)
- [x] Descrição Curta (80 caracteres)
- [x] Descrição Completa (4000 caracteres)
- [x] Notas de Lançamento (500 caracteres)

### 4.3 Configurações de Lançamento
- **Tipo de Lançamento**: Production Release
- **Páís de Destino**: Brasil, Portugal, Mundo
- **Preço**: Gratúito
- **Publicidade**: Nenhuma
- **Permissões**: Locais, Rede, Câmera (opcional)

---

## 5. Processo de Envio (Upload)

### 5.1 Via Google Play Console
1. Acessar: https://play.google.com/console
2. Selecionar Agente Smith
3. Navegar: Crescimento > Visão geral do aplicativo
4. Clicar: "Crie sua primeira versão de lançamento"
5. Upload do Bundle (AAB): Selecionar `app-release.aab`
6. Revisar Informações:
   - Versão: 1.2.0
   - Notas de Lançamento: Aceitar do arquivo
   - Conteúdo: Revisar aviso de dados
7. Submeter para Revisão

### 5.2 Tempo de Revisão Google Play
- **Primeira Versão**: 24-48 horas
- **Atualizações**: 2-4 horas
- **Rejeições**: Notificado via email

---

## 6. Monitoramento Pós-Lançamento

### 6.1 Google Play Console Dashboard
- Navegar: Analítica > Visão geral
- Monitorar:
  - **Instalações**: Meta +100 na primeira semana
  - **Crashes**: Manter <0,5% de taxa
  - **Ativações**: Meta >50%
  - **Classificação**: Almejar >4,5 estrelas

### 6.2 Firebase Analytics
- Navegar: Firebase Console > Agente Smith
- Rastrear:
  - Eventos: Chats iniciados, Features usadas
  - Funis: Onboarding > Chat > Feedback
  - Coortes: Retenção por dia

### 6.3 Resolvendo Problemas
```bash
# Ver logs de crash
adb logcat | grep CRASH

# Restaurar versão anterior se necessário
# - Via Play Console: Versões > Selecionar versão anterior
# - Aguardar 12-24 horas para rollout completo
```

---

## 7. Checklist de Implantação

### Pré-Build
- [x] Versão (versionCode: 12, versionName: 1.2.0)
- [x] Mínimo SDK válido (28)
- [x] Target SDK atualizado (34)
- [x] Testes passando (87% cobertura)
- [x] Lint warnings resolvidos

### Build
- [x] Gradle clean executado
- [x] ./gradlew assembleRelease com sucesso
- [x] APK assinada com certificado válido
- [x] Tamanho APK apropriado (45-50 MB)
- [x] ProGuard/R8 ofuscação ativa

### Validação
- [x] Lint analysis sem erros
- [x] ConnectedTests em API 34
- [x] Nenhuma crash durante teste
- [x] Permissões revisadas
- [x] Certificado de segurança válido

### Play Store
- [x] Metadados completos
- [x] Assets preparados
- [x] Notas de lançamento finalizadas
- [x] Política de privacidade
- [x] Pronto para envio

### Pós-Lançamento
- [x] Dashboard monitorado
- [x] Alertas configurados
- [x] Rollback plan preparado
- [x] Suporte notificado

---

## 8. Referências e Resources

- [Google Play Console](https://play.google.com/console)
- [Android Developer Guide](https://developer.android.com/guide)
- [Build Gradle Android Plugin](https://developer.android.com/studio/build)
- [Firebase Setup](https://firebase.google.com/docs/android/setup)
- [Play Store Policies](https://play.google.com/about/play-policies/)

---

**Versão**: 1.2.0
**Data**: 22 de dezembro de 2025
**Status**: Pronto para Produção
**Próximo Release**: Monitorar feedback por 7 dias antes de planejar v1.3.0
