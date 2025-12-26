# SOLUCAO RAPIDA: Could not load module <Error module>

## O Erro
```
e: Could not load module <Error module>
FAILURE: Build failed with an exception.
Execution failed for task ':app:kaptGenerateStubsDebugKotlin'.
Compilation error. See log for more details
```

## Causa (99% dos casos)
- Uma classe foi deletada mas referencia ainda existe
- Um import esta apontando para classe que nao existe
- Uma dependencia nao foi adicionada
- Um modulo de Git submodule nao foi atualizado

---

## SOLUCAO RAPIDA (3 minutos)

### Passo 1: Desabilitar KAPT para ver ERRO REAL

Edite `build.gradle.kts` (raiz):

```kotlin
allprojects {
    tasks.matching { it.name.contains("kapt") }.configureEach {
        enabled = false
    }
}
```

### Passo 2: Rebuild (agora vai aparecer o ERRO REAL)

```bash
./gradlew clean
./gradlew build
```

**Procure na saida por:**
- `error: cannot find symbol`
- `error: unresolved reference`
- `class X not found`
- `cannot access class`

### Passo 3: Corrigir o Erro Real

**Exemplo 1 - Classe deletada:**
```
error: cannot find symbol: class MyDeletedClass
```
→ Procure por `MyDeletedClass` em todo projeto (Ctrl+Shift+F)
→ Delete as referencias

**Exemplo 2 - Import errado:**
```
error: cannot find symbol: class FirebaseDatabase
```
→ Verifique se `firebase-database` esta em `build.gradle.kts`
→ Adicione: `implementation("com.google.firebase:firebase-database-ktx")`

**Exemplo 3 - Git Submodule:**
```bash
git submodule update
```

### Passo 4: Remover o codigo que adicionou

Delete este bloco de `build.gradle.kts`:

```kotlin
allprojects {
    tasks.matching { it.name.contains("kapt") }.configureEach {
        enabled = false
    }
}
```

### Passo 5: Rebuild FINAL

```bash
./gradlew clean
./gradlew build
```

✅ **BUILD SUCCESS!**

---

## Se ainda nao funcionar...

### Limpeza Total

```bash
# Limpe tudo
rm -rf .gradle
rm -rf app/.gradle
rm -rf build
./gradlew clean

# No Android Studio:
# File → Invalidate Caches → Restart

# Reconstrua
./gradlew build
```

### Debug Avancado

```bash
./gradlew build --info
./gradlew build --debug
./gradlew build --stacktrace
```

---

## Checklist

- [ ] Desabilitou KAPT
- [ ] Viu o ERRO REAL na saida
- [ ] Corrigiu a classe/import/dependencia
- [ ] Removeu codigo de desabilitar KAPT
- [ ] Rebuild passou com sucesso
- [ ] App compilando 100%
