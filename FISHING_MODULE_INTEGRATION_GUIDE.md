# Guia de Integração do Módulo de Pesca - Android Studio

## 📱 Visão Geral

Este documento descreve como integrar e implementar o novo módulo de Pesca Inteligente no projeto Agente-Smith-Android usando Android Studio.

O módulo inclui:
- **GPSLocationManager**: Rastreamento de localização em tempo real
- **FishingWeatherManager**: Análise de vento, maré e fases lunares
- **FishingAssistant**: Sistema completo de recomendações de pesca

---

## 🔧 Configuração no Android Studio

### 1. Estrutura de Diretórios

```
app/src/main/java/com/dutra/agente/essencial/
├── localizacao/
│   └── GPSLocationManager.kt
├── pesca/
│   ├── FishingWeatherManager.kt
│   └── FishingAssistant.kt
└── ... (outros módulos)
```

### 2. Permissões AndroidManifest.xml

Adicione as seguintes permissões ao `AndroidManifest.xml`:

```xml
<!-- Permissões de Localização -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Permissões de Internet (para futuras APIs de clima) -->
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Solicitação de Permissões em Runtime

Para Android 6.0 (API 23) e superior, implemente solicitação de permissões em tempo de execução:

```kotlin
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Solicitar permissões
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                initFishingModule()
            }
        }
    }
    
    private fun initFishingModule() {
        // Inicializar módulo de pesca
    }
}
```

---

## 🎯 Integração com Hilt (Dependency Injection)

Para integração com Hilt, crie um módulo de fornecimento:

```kotlin
// FishingModule.kt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import android.content.Context

@Module
@InstallIn(SingletonComponent::class)
object FishingModule {
    
    @Provides
    fun providesGPSLocationManager(context: Context): GPSLocationManager {
        return GPSLocationManager(context)
    }
    
    @Provides
    fun providesFishingWeatherManager(): FishingWeatherManager {
        return FishingWeatherManager()
    }
    
    @Provides
    fun providesFishingAssistant(context: Context): FishingAssistant {
        return FishingAssistant(context)
    }
}
```

Anote o seu Activity com `@HiltActivity`:

```kotlin
@HiltActivity
class MainActivity : AppCompatActivity() {
    // ...
}
```

---

## 📍 Uso Básico

### Exemplo 1: Obter Recomendações de Pesca Próximas

```kotlin
class FishingActivity : AppCompatActivity() {
    
    @Inject
    lateinit var fishingAssistant: FishingAssistant
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fishing)
        
        // Obter 3 melhores locais de pesca próximos
        val recommendations = fishingAssistant.getFishingRecommendations(maxResults = 3)
        
        recommendations.forEach { rec ->
            println("Local: ${rec.spot.name}")
            println("Distância: ${rec.distance}m")
            println("Rumo: ${rec.bearing}°")
            println("Qualidade Geral: ${rec.conditions.overallQuality}")
            println("Espécies Recomendadas: ${rec.recommendedSpecies.joinToString(", ")}")
            println("Nível de Risco: ${rec.riskLevel}")
            println("---")
        }
    }
}
```

### Exemplo 2: Rastreamento de Localização em Tempo Real

```kotlin
class LiveFishingActivity : AppCompatActivity() {
    
    @Inject
    lateinit var fishingAssistant: FishingAssistant
    
    override fun onStart() {
        super.onStart()
        
        // Iniciar rastreamento com callback
        fishingAssistant.startLocationTracking { recommendation ->
            if (recommendation != null) {
                updateUI(recommendation)
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        // Parar rastreamento para economizar bateria
        fishingAssistant.stopLocationTracking()
    }
    
    private fun updateUI(rec: FishingRecommendation) {
        // Atualizar UI com recomendação mais próxima
        textViewLocation.text = "Local: ${rec.spot.name}"
        textViewDistance.text = "Distância: ${rec.distance}m"
        textViewQuality.text = "Qualidade: ${rec.conditions.overallQuality}"
    }
}
```

### Exemplo 3: Gerar Relatório Detalhado

```kotlin
val spot = FishingSpot(
    name = "Rio Doce",
    latitude = -20.39,
    longitude = -42.91,
    description = "Excelente para dourados",
    type = "Rio",
    bestSeason = "Primavera/Verão"
)

val report = fishingAssistant.generateDetailedReport(spot)
println(report)
```

---

## 🏗️ Integração com Activity/Fragment

### Opção 1: Activity

```kotlin
@HiltActivity
class FishingMainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var fishingAssistant: FishingAssistant
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fishing_main)
        
        // Inicializar componentes de pesca
        setupFishingUI()
    }
    
    private fun setupFishingUI() {
        // Implementar UI para módulo de pesca
    }
}
```

### Opção 2: Fragment

```kotlin
@HiltViewModel
class FishingViewModel @Inject constructor(
    private val fishingAssistant: FishingAssistant
) : ViewModel() {
    
    private val _recommendations = MutableLiveData<List<FishingRecommendation>>()
    val recommendations: LiveData<List<FishingRecommendation>> = _recommendations
    
    fun loadRecommendations() {
        viewModelScope.launch {
            val recs = fishingAssistant.getFishingRecommendations()
            _recommendations.value = recs
        }
    }
}

class FishingFragment : Fragment() {
    
    private val viewModel: FishingViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.loadRecommendations()
        viewModel.recommendations.observe(viewLifecycleOwner) { recommendations ->
            updateUI(recommendations)
        }
    }
    
    private fun updateUI(recommendations: List<FishingRecommendation>) {
        // Implementar UI com RecyclerView, etc
    }
}
```

---

## 📊 Testes Unitários

Exemplo de testes para o módulo de pesca:

```kotlin
class FishingAssistantTest {
    
    private lateinit var context: Context
    private lateinit var fishingAssistant: FishingAssistant
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fishingAssistant = FishingAssistant(context)
    }
    
    @Test
    fun testGetFishingRecommendations() {
        val recommendations = fishingAssistant.getFishingRecommendations(maxResults = 3)
        assertEquals(3, recommendations.size)
    }
    
    @Test
    fun testGenerateDetailedReport() {
        val spot = FishingSpot(
            name = "Rio Teste",
            latitude = -20.0,
            longitude = -45.0,
            description = "Teste",
            type = "Rio",
            bestSeason = "Verão"
        )
        
        val report = fishingAssistant.generateDetailedReport(spot)
        assertTrue(report.contains("RELATORIO DE PESCA"))
        assertTrue(report.contains("Rio Teste"))
    }
}
```

---

## 🚀 Próximas Melhorias

1. **Integração com APIs de Clima Reais**
   - OpenWeatherMap para dados de vento
   - NOAA/Marinha para dados de maré

2. **Machine Learning**
   - Aprender padrões de pesca do usuário
   - Recomendações personalizadas baseadas em histórico

3. **UI/UX**
   - Mapa interativo com spots de pesca
   - Histórico de pescarias
   - Compartilhamento de dados com outros pescadores

4. **Sincronização Cloud**
   - Firebase Realtime Database para spots customizados
   - CloudMessaging para notificações

---

## ✅ Checklist de Implementação

- [x] Criar classe GPSLocationManager
- [x] Criar classe FishingWeatherManager
- [x] Criar classe FishingAssistant
- [x] Adicionar permissões ao AndroidManifest.xml
- [x] Implementar Hilt para DI
- [x] Criar exemplos de uso
- [x] Documentar integração
- [ ] Escrever testes unitários
- [ ] Criar UI no Android Studio
- [ ] Integrar com real APIs de clima

---

**Desenvolvido por**: Capitão Comet
**Data**: 22 de dezembro de 2025
**Status**: Pronto para Implementação no Android Studio ✅
