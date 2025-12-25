# ERRO kaptGenerateStubsDebugKotlin - GUIA DE CORREÇÃO

## PROBLEMA IDENTIFICADO

Erro: `Task :app:kaptGenerateStubsDebugKotlin FAILED`  
Causa: Conflito de módulos ou classes duplicadas no Kotlin Annotation Processing

---

## SOLUÇÃO RÁPIDA (EXECUTE NA SEQUÊNCIA)

### PASSO 1: Limpar Cache Gradle

```bash
# No Terminal/PowerShell do Android Studio:
./gradlew clean
rm -rf .gradle
rm -rf app/build
```

### PASSO 2: Invalidar Caches Android Studio

1. Abra Android Studio
2. File > Invalidate Caches
3. Selecione: "Invalidate and Restart"
4. Aguarde reinicialização

### PASSO 3: Verificar Gradle Properties

Editar `gradle.properties`:

```properties
# Aumentar memória para KAPT
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=1024m

# Habilitar cache paralelo
org.gradle.parallel=true
org.gradle.workers.max=4

# Desabilitar daemon se tiver problemas
org.gradle.daemon=true
```

### PASSO 4: Sincronizar Gradle

```bash
# Terminal:
./gradlew --refresh-dependencies
./gradlew build --info
```

### PASSO 5: Verificar Imports Duplicados

Verifique se NÃO há classes com mesmo nome em diferentes pacotes:

```kotlin
// ❌ ERRADO - Classes duplicadas
com.dutra.agente.domain.ErrorHandler
com.dutra.agente.presentation.ErrorHandler  // Mesmo nome!

// ✅ CORRETO - Nomes únicos
com.dutra.agente.domain.ErrorHandler
com.dutra.agente.util.ErrorLogger  // Nome diferente
```

---

## SOLUÇÃO COMPLETA SE PROBLEMA PERSISTIR

### 1. Limpar Completamente

```bash
# Terminal/PowerShell:
cd C:\caminho\para\Agente-Smith-Android

# Windows:
rmdir /s .gradle
rmdir /s app\build
rmdir /s gradle\caches
rmdir /s build

# macOS/Linux:
rm -rf .gradle
rm -rf app/build
rm -rf gradle/caches
rm -rf build
```

### 2. Redownload Gradle Wrapper

```bash
# Terminal:
./gradlew wrapper --gradle-version 7.5
```

### 3. Reconstruir

```bash
./gradlew clean
./gradlew build
```

---

## VERIFICAR APP/BUILD.GRADLE

Confirme que seu `app/build.gradle` tem:

```gradle
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'  // ← IMPORTANTE
}

android {
    // ...
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = '11'
    }
}

dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.5.31"
    implementation "org.jetbrains.kotlin:kotlin-reflect:1.5.31"
    
    // Room Database (se usar)
    implementation "androidx.room:room-runtime:2.3.0"
    kapt "androidx.room:room-compiler:2.3.0"
    
    // Outras dependências...
}
```

---

## VERIFICAR POSSÍVEIS CONFLITOS DE DEPENDÊNCIAS

```bash
# Ver dependências com conflito:
./gradlew app:dependencies

# Ver árvore de dependências:
./gradlew app:dependencyInsight --configuration debugCompileClasspath
```

---

## SE AINDA TIVER ERRO: EXECUTAR COM DEBUG

```bash
# Terminal com output detalhado:
./gradlew build --debug 2>&1 | grep -i "error\|failed"

# Ou ver log completo:
./gradlew build --scan
```

---

## CHECKLIST DE VERIFICAÇÃO

- [x] Gradle clean executado
- [x] Cache invalidado
- [x] gradle.properties atualizado
- [x] Sem classes duplicadas
- [x] KAPT plugin habilitado
- [x] JDK 11+ configurado
- [x] Kotlin 1.5+ utilizado
- [x] Sem dependências conflitantes

---

## APÓS RESOLVER O ERRO

1. Executar novo build:
   ```bash
   ./gradlew clean build
   ```

2. Sincronizar Gradle novamente
3. Tentar rodar app:
   ```bash
   ./gradlew installDebug
   ```

---

## SE NENHUMA SOLUÇÃO FUNCIONAR

Última opção - Recriar projeto:

```bash
# Backup atual
cp -r Agente-Smith-Android Agente-Smith-Android.bak

# Deletar gradle wrapper
rm -rf gradle .gradletasknamecache

# Resetar git (se necessário)
git clean -fdx
git reset --hard HEAD

# Redownload tudo
./gradlew build
```

---

**Status:** Guia de correção pronto  
**Próximo passo:** Execute PASSO 1-5 acima
