# MarketMogul
Quick and lightweight stock monitoring app.

This app uses http requests to get JSON data from Google.

It does not validate tickers - Google sends back an error.

It makes the http requests each minute (hard coded), and does not make the requests when 
the app is not in foreground.  There is a boolean that gets toggled in onPause/onResume which controls the updating process.

NOTES:
* US equities (seem to) work.  
* If there is a bad ticker, the time field is replaced by "ERROR". 
* some tickers automatically "re-map".  RHAT, Redhat's old (NASDAQ) ticker gets remapped somewhere 
on the server to "RHT" - their new ticker on the NYSE.  I think I should update the database to 
reflect the ticker change, or indicate the change in the UI.
* Indexes are:
  * DJ Industrials .dji (looks like it's 15 minutes delayed).
  * SPX .inx (realtime-ish).
  * NASDAQ composite .ixic  (also realtime-ish).

TODO: 
* Definitely need to get a better data source.  Something we can use for ticker lookup.  Also it would be nice to get the volume.  
* Edit screen is a mess - it needs UI improvements.
* Update timer is hard coded to 60 seconds.  Should this be variable?
* Do something with the "About" screen. 
* Should we track amount of data sent and received?
* There is no limit on the number of tickers; it's in a ScrollView (and a HorizontalScrollView). Is this going to be a problem?  Currently testing with 12-15 tickers.

Done:
* Implement two-column layout when in landscape mode.
* fix bug in langscape mode if there is an off number of securities - last security does not get displayed.
* add a few initial entries to the database.
* fix bug causing double updates.
* implemented progress bar, for start up, and when returning from the "Edit" screen, but it does not display when normal (timed) updates occur.
* Two-column layout is all done. 
* Added "up as home" in the left corner of the action bar for Edit and About screens.
