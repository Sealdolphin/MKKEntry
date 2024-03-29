# Mi ez?
Az MKKEntry egy egyszerű beléptetőrendszer, alapvetően a Műegyetemi Katolikus Közösség számára készült, hogy a beléptetést gyorsítsa.
A program egy összetettebb adatbázist kezel, illetve személyreszabott profilokon keresztül a gyűjtött adatokról statisztikákat is készít.
Az adatok gyűjtése és a statisztikák készítése elsősorban egy beléptetéshez igazodik.
A programba importálható meglévő táblák is, illetve adatstruktúrák.
A program képes soros-porton olvasó eszközökról is parancsokat olvasni.
***DISCLAIMER: A lent leírtak a kész verzió tulajdonságait tükrözik. A program még fejlesztés alatt áll, ezért a leírtak változhatnak, illetve a megvalósításban eltérhetnek.***

# Telepítés
Hamarosan...

# Hogyan működik?
Indítás után az alapvető beállításokat tölti be, amik eltölthetőek a mappából.
Ennek hiányában a működéshez létre kell hozni egy új profilt (lásd lentebb).
A betöltés után a program üzemképes, folyamatosan fogadja az adatokat.
Az újonnan leolvasott kódokhoz a vonalkód olvasót kell használni, illetve kézileg is elküldhető egy kód.

## Működéssel kapcsolatos tudnivalók
- Minden újonnan érkező kódot a rendszer egy új vendégként kezel, _**és**_ azonnal beléptet.
- A kódok a vendégek egyedi azonosítói ezért a rendszer **nem regisztrál kétszer** ugyanolyan kódot.
- A bevitt kódoknak meg kell felelniük a profilban beállított **maszknak**. Különben a kód érvénytelen.
- Egyes kódok *parancsként* funkcionálnak, leolvasásuk után az alsó sorban változik a információs panel.

## Általános működés
1. Telepítsd az alkalmazást
2. Töltsd be a megfelelő profilt (vagy hozz létre egyet)
3. Töltsd fel a regisztrált vendégeket (külső CSV fájlból)
4. Csatlakoztass egy vonalkód olvasót (Soros porton / USB-n) - _Opcionális_
5. Minden kész! Indulhat a beléptetés.

# Első lépések
A beléptetés felállításához néhány előkészület szükséges. Ehhez összeírtam néhány típuspéldát, ami alapján könnyedén felépíthető és személyreszabható a saját beléptetőrendszerünk.

**Regisztrált vendégek:**
Ha az eseményre lehett előzetesen regisztrálni, akkor a már bejelentkezett, de még be nem lépett vendégeket érdemes felvenni a rendszerbe.
Az *Importálás* segítségével a saját adatbázisunkból készített fájlt a program feldolgozza és automatikusan felveszi a vendégeket a listába. Fontos:
- Az adatbázist olyan formátumra kell hozni, amit a rendszer képes értelmezni (lásd kompatibilitás)
- Vigyázzunk, hogy ne legyen olyan rekord, amit esetleg már korábban felvettünk
- Felvehetünk olyan rekordokat is, amikhez tartozik belépési, vagy kilépési időpont
- Az importálási folyamatot visszavonhatjuk

**Beléptetés / Kilépés:**
Az esemény folyamán a beléptetés folyamata egyszerű.
1. Ha a vendég rendelkezik kóddal, akkor le kell olvasni a belépési kódját.
2. Ha a vendégnél nincs kód, akkor a rendszer képes a maszk alapján egy újat generálni *(Fejlesztés alatt)*
3. Ha vendég kedvezményekkel rendelkezik, akkor a belépés **után** le kell olvasni a megfelelő kedvezmény kódját a *Kedvezmények* részről. (Kézileg is beüthető a parancs kódja)
4. *Kilépéshez és törléshez* a kód leolvasása **előtt** le kell olvasni a kilépés parancskódját, majd a vendég kódját.
5. Törlés esetén a program **eltávolítja** a rekordot a táblából.
6. Kilépett vendég kódjának leolvasása után a vendég *újra beléphet*. Ilyenkor a kilépés dátum törlődik és a belépés dátuma **felülíródik**. (Ez a funkció módosítható a profilokban)
7. Parancskód leolvasása után a program **csak a következő** kódot olvassa a parancs szerint, utána visszaáll belépési módra

**Kódleolvasási problémák**
- *A kód nem megfelelő formátumú:* A leolvasott / beütött kód nem felel meg a pofilban megadott maszknak. Ellenőrizd a profilbeállításokat, vagy a kódot.
- *A vendég már belépett:* A kódhoz tartozó vendéget egyszer már leolvasták, és még nem lépett ki.
- *A vendég még nem lépett be:* A leolvasott kódhoz nem tartozik belépési dátum.
- *A vendég meghaladta a belépési kvótát:* A vendég már kilépett, ezért nem léphet be újra *(Fejlesztés alatt)*

# Profilbeállítások
A programban a profilokon keresztül *személyreszabott* beállításokat lehet tárolni. Ezzel nem kell minden indítás előtt foglalkozni az eseményhez tartozó adatok újbóli megadásával. A programban egyszerre több profil is beállítható, de egyszerre **csak egy profil használható**. A profil váltása után a program **minden rekordot töröl**. A profilhoz tartozó adatok:
- Különböző jegytípusok
- Különböző kedvezmények
- ~~Exportálási filterek *(fejlesztés alatt)*~~
- A Profilt jellemző belépési kód maszkja
- Az eseményt jellemző beléptetési konvenciók és szabályok (belépési kvóták stb)

### Profil betöltése fájlból
Abban az esetben, ha nem áll rendelkezésre soros-port olvasó eszköz, lehet külső fájlból is beolvasni profilt.
Sablon:
```json
{
  "profiles" : [
    {
      "name" : "MKK",
      "mask" : "[0-9]{4}",
      "commands" : {
        "default" : "MKK",
        "leave" : "LEAVE_GUEST",
        "delete" : "DELETE_GUEST"
      },
      "discounts" : [],
      "tickets" : [],
      "barCodes" : [],
      "defaultType" : "Helyszíni"
    }
  ],
  "active" : "MKK",
  "version" : "v1.3"
}
```
#### Magyarázat:
- _profiles_: a felsorolt profilok listája
- _name_ : a profil neve
- _mask_ : a profilra illeszkedő belépő kód maszkja (Reguláris kifejezés)
- _commands_ : a profilhoz tartozó alapparancsok
  - _default_ : a belépőkód maszk eleje
  - _leave_ : a kiléptetés parancsa
  - _delete_ : a törlés parancsa
- _discounts_ : a profilhoz tartozó kedvezmények listája
- _tickets_ : a profilhoz tartozó jegytípusok listája
- _barCodes_ : a profilhoz tartozó vonalkódok listája
- _defaultType_ : az alapértelmezett jegytípus (ismeretlen rekord esetén)
- _active_ : Aktív profil
- _version_ : Program verziószáma

#### Kedvezmények
```json
{
  "name" : "Büfé",
  "icon" : "Icons\tDIS_PIE.png",
  "barCode" : "FOOD_SALE",
  "discount" : 500
}
```
- _name_ : A kedvezmény rövid neve (és azonosítója!!)
- _icon_ : A hozzá tartozó ikon relatív útvonala
- _barCode_ : A hozzá tartozó vonalkód parancsa
- _discount_ : A kedvezmény mennyisége (lehet negatív is)
#### Jegytípusok
```json
{
  "name" : "VIP",
  "price" : 0,
  "fee" : true
}
```
- _name_ : A jegytípus rövid neve (és azonosítója!!)
- _price_ : A jegytípus ára
- _fee_ : Számít-e a jegytípus a kasszában?

#### Vonalkódok
```json
{
  "name" : "Büfé",
  "description" : "Hozott sütit, vagy üdítőt",
  "imagePath" : "Barcodes\tfoodSale.png",
  "meta" : "FOOD_SALE"
}
```
- _name_ : A vonalkód neve
- _description_ : A vonalkódhoz tartozó leírás (ez jelenik meg az oldalsávon is)
- _imagePath_ : A vonalkód képének relatív útvonala
- _meta_ : A vonalkódhoz tartozó parancs (egyben azonosító is)


### Profil létrehozása
Új profilt is létre lehet hozni akár a program indulása során is. **FONTOS**: ehhez a funkcióhoz soros-port olvasó eszköz szükséges.

**Profil létrehozása**
Az új profil létrehozásához szükséges megadni a *nevet* és a belépési kód *maszkját*, amik alapján a profil azonosítható, illetve fel kell venni *legalább egy* darab jegytípust, ami az *alapméretezett* jegytípus lesz. Minden más tulajdonságot működés közben is lehet módosítani. Fontos, hogy a program nem tud tárolni két azonos nevű profilt. Profilokat lehet importálni külső adatfájlokból is. (A szükséges JSON-tagekhez lásd: a JSON dokumentációt)

**Jegytípusok**
A különböző jegytípusok a könnyebb és részletesebb statisztika elkészítésére szolgálnak. Az újonnan felvett vendégeket a rendszer automatikusan az *alapméretezett* jegytípusba rendelni. Importálásnál, ha az importált rekord nem felel meg egyik felvett típusnak sem, akkor kézileg eldönthető egy új típus létrehozása, vagy egy már meglévő választása. A statisztika elkészítésénél a program figyelembe veszi a jegytípushoz tartozó *árat*, illetve azt, hogy beleszámít-e a kasszába (*kassza*).

**Kedvezmények**
A különböző kedvezményeket helyben lehet alkalmazni minden vendégre *egyénileg*. Közvetlen a belépés után, illetve a *rekord kijelölése* után a táblában, a megfelelő kódot leolvasva módosíthatjuk a vendég jegyének árát *anélkül*, hogy másik jegytípushoz rendelnénk. A vendéghez tartozó kedvezmények a **részletek** gomb után jelennek meg. A kedvezmény a kód leolvasása után aktiválódik, újbóli leolvasás után pedig visszavonódik.

**Exportálási filterek**
A különböző exportálási filterekkel *Excel-barát* (.csv, .txt) fájlok készíthetőek a meglévő adatbázis rekordjairól. A filterek egyes tulajdonságra képesek szűrni, illetve a nekünk megfelelő kiterjesztésben menti az adatfájlt.

**Belépési kód maszkja**
A belépési kód maszkja egy olyan *reguláris kifejezés*, amivel a profilra (eseményre) jellemző belépési kódokat általánosan meghatározza. A maszk létrehozásához használható a *maszk-varázsló*, illetve a Java nyelvben használt kifejezések. Fontos, hogy ennek az átállítása csak a program **újraindítása után** lép érvénybe.

**Belépéséi konvenciók és szabályok**
Hamarosan...

# Kérdések és visszajelzések
Bármilyen probléma és hiba esetén írható *issue* a githubon: https://github.com/Sealdolphin/MKKEntry, illetve megkereshetsz emailen:
mihalovits.mark@gmail.com
