# MarketMogul
Quick and lightweight stock monitoring app.

This app uses http requests to get JSON data.  

It does not validate tickers.

It makes the http requests each minute (hard coded), and does not make the requests when 
the app is not in foreground.  there is a boolean that gets toggled in onPause/onResume.

NOTES:
* US equities (seem to) work.  
* If there is a bad ticker, the time field is replaced by "ERROR". 
* some tickers automatically "re-map".  RHAT, Redhat's old ticker gets remapped somewhere 
on the server to "RHT" - their new ticker on the NYSE.  I think I should update the database to 
reflect the ticker change.
* the only index i've tested is DJ Industrials.  use .dji

TODO: 
* definitely need to get a better data source.  something we can use for ticker lookup.  
