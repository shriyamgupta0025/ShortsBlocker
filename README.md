# Shorts Blocker (Android App)

YouTube Shorts + Instagram Reels (WhatsApp se forward ki hui reel bhi Instagram mein hi
khulti hai, so wo automatically cover ho jaati hai) par bitaya gaya time track karta hai.
Daily 1 ghanta (dono milakar) free hai. Uske baad:

1. Screen par ek English message aayega ("Your Shorts/Reels limit for today has ended.")
2. 15 second ke andar nahi rukoge to Shorts/Reels screen se turant wapas bhej dega
3. Uske baad poore din jab bhi Shorts (YouTube) ya Reels (Instagram) khologe, turant wapas bhej dega
4. Agli raat 12 baje (midnight) sab reset ho jata hai

**Important: Normal YouTube video dekhna (padhai wale video, lambi videos) kabhi
block nahi hota — sirf Shorts tab/screen block hoti hai.** Isliye YouTube pe padhai
karne mein koi rukawat nahi aayegi, chahe aapka Shorts/Reels ka time khatam ho chuka ho.

## Sirf phone se build karna (bina computer ke)

Is folder mein `.github/workflows/build.yml` already daal diya gaya hai — GitHub par
push karte hi ye khud APK bana dega. Neeche pura tarika hai.

## Kaise install karein (agar computer/laptop hai)

1. Android Studio (free, Google se) install karo apne computer par.
2. Ye poora `ShortsBlocker` folder Android Studio mein **Open** karo (File → Open).
3. Pehli baar "Gradle Sync" hone dein (internet chahiye, 2-5 min lag sakta hai).
4. Phone ko USB se connect karo, phone mein **Developer Options → USB Debugging** ON karo.
5. Android Studio mein upar green **Run ▶** button dabao — app phone mein install ho jayegi.

(Agar Android Studio use nahi karna, koi bhi Android developer se ye folder dekar
ek APK banwa sakte ho — process wahi rahega, bas ek baar ka kaam hai.)

## App install hone ke baad

1. App kholo.
2. **"Accessibility Service ON karo"** button dabao → list mein "Shorts Blocker" dhundo
   → usko ON karo. Phone warning dega ki ye app screen padh sakti hai — ye normal hai,
   isi permission se ye Shorts/Reels detect karta hai.
3. **"Overlay Permission ON karo"** button dabao → "Allow display over other apps" ON karo.
4. Bas. Ab app background mein khud kaam karegi, dobara kholne ki zaroorat nahi.

## Limitations (imaandaari se batana zaroori hai)

- Ye sirf **Android** ke liye hai. iPhone (iOS) par Apple itni deep permission nahi deta,
  isliye ye approach iPhone par kaam nahi karegi.
- Shorts/Reels detect karne ka tareeka YouTube/Instagram ki current (2026) app design par
  based hai. Agar YouTube ya Instagram apna app update karke internal design badal de,
  to ho sakta hai detection kaam karna band kar de. Aisa hua to
  `ShortsBlockerService.kt` file mein `shortsReelsIds()` function ki id list update
  karni padegi.
- Google Play Store par is tarah ki app publish karna mushkil hai (Accessibility Service
  policy strict hai) — isiliye ye sirf apne phone mein direct install (sideload) karne
  ke liye hai, Play Store ke liye nahi.
- "1 ghanta" ka number `UsageTracker.kt` file mein `DAILY_LIMIT_SECONDS` badal kar
  aasani se change kiya ja sakta hai.
