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
* Indexes are 
* DJ Industrials .dji (looks like it's 15 minutes delayed).
* SPX .inx (realtime-ish).
* NASDAQ composite .ixic  (also realtime-ish).
TODO: 
* Definitely need to get a better data source.  Something we can use for ticker lookup.  Also it would be nice to get the volume.  
* See if it's feasible to use a two-column layout when in landscape mode on a tablet.
* There is no limit on the number of tickers; it's in a scrollview. Is this going to be a problem.  Currently testing with 12-15 tickers.
