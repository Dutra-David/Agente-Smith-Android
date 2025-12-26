# PROMPT PARA AGENTE AI - RESOLUCAO DE ERRO KAPT NO ANDROID STUDIO

## CONTEXTO DO PROBLEMA
O projeto Agente-Smith-Android está com erro de compilação Kotlin:
- Erro: `Execution failed for task ':app:kaptGenerateStubsDebugKotlin'`
- Causa: Incompatibilidade entre versões Java (1.8 vs 17)
- Local: Android Studio, durante build do projeto

## INSTRUCOES PARA O AGENTE (PASSO A PASSO)

### FASE 1: DIAGNOSTICO
1. Abra o arquivo `app/build.gradle.kts` ou `app/build.gradle`
2. Procure pelos blocos:
   - `compileOptions {}`
   - `kotlinOptions {}`
3. Verifique se as versões estão desalinhadas

### FASE 2: CORRECAO - ARQUIVO: app/build.gradle.kts

LOCALIZE o bloco `android {}` dentro do arquivo `app/build.gradle.kts`

ADICIONE OU CORRIJA as seguintes secoes:

```kotlin
android {
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.agente.smith.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    // ADICIONE ISTO - Compatibilidade Java
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    // ADICIONE ISTO - Compatibilidade Kotlin
    kotlinOptions {
        jvmTarget = "11"
    }
}
```

### FASE 3: LIMPEZA E REBUILD

1. Executar Clean: Build -> Clean Project
2. Reconstruir Projeto: Build -> Rebuild Project
3. Sincronizar Gradle: File -> Sync Now

### FASE 4: VERIFICACAO

Sucesso: Build completo sem erros de KAPT
Falha: Se persistir, verificar:
   - Versao do Android Gradle Plugin (deve ser 7.4.2+)
   - Versao do Kotlin (deve ser 1.8.0+)
   - Dependencias conflitantes

## VALORES OPCIONAIS

Se Java 11 nao funcionar, tente:

Opcao 2: Java 1.8
compileOptions { sourceCompatibility = JavaVersion.VERSION_1_8 }
kotlinOptions { jvmTarget = "1.8" }

Opcao 3: Java 17
compileOptions { sourceCompatibility = JavaVersion.VERSION_17 }
kotlinOptions { jvmTarget = "17" }

## CHECKLIST PARA O AGENTE

- Localizou o arquivo app/build.gradle.kts
- Identificou os blocos compileOptions e kotlinOptions
- Adicionou/Corrigiu as configuracoes para Java 11
- Executou gradle clean
- Executou gradle build com sucesso
- Verificou que nao ha erros de KAPT
- Testou build do projeto (Rebuild Project)

## RESULTADO ESPERADO

Após seguir este prompt, o agente deve:
1. Compilar sem erros de KAPT
2. Gerar o APK com sucesso
3. Estar pronto para testes no dispositivo

---
Criado em: 2025-12-26
Status: Ativo e testado
Compativel com: Android Studio Electric Eel+, Kotlin 1.8.0+, Gradle 8.0+
