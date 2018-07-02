# NWTiS_projekt

### Aplikacija_1 <br />
* web poslužitelj: Tomcat <br />
* EE osobine: EE6/7 Web <br />
* korisničko sučelje: JSF (facelets) <br />
* baza podataka: MySQL - naziv nwtis_{korisnickoime}_bp_1 <br />
* rad s bazom podataka: JDBC, SQL <br />
* šalje email poruku <br />
* daje socket server <br />
* daje SOAP web servis za meteorološke podatke izabranih adresa <br />
* daje REST web servis za prognostičke podatke izabranih adresa <br />
* koristi IoT_Master web servis za upravljanje IoT uređajima <br />
* koristi openweathermap.org REST web servis za preuzimanje meteoroloških podataka <br />
* koristi Google Maps API REST web servis za preuzimanje geolokacijskih podataka za adresu <br />

<br />

### Aplikacija_2 <br />
* Web poslužitelj: Glassfish <br />
* EE osobine: EE7 <br />
* korisničko sučelje: JSF (facelets) <br />
* baza podataka: JavaDB – naziv nwtis_{korisnickoime}_bp_2 <br />
* rad s bazom podataka: ORM (EclipseLink) <br />
* James email poslužitelj <br />
* pregledava i briše email poruke <br />
* koristi socket server {korisnicko_ime}_aplikacija_1 za upravljanje socket serverom 
* upravljanje grupom i upravljanje IoT uređajima iz grupe <br />
* koristi SOAP web servis {korisnicko_ime}_aplikacija_1 za meteorološke podatke za odabrani IoT uređaj, za adresu odabranog IoT uređaja <br />
* koristi REST web servis {korisnicko_ime}_aplikacija_1 za pregled, upravljanje i autenticiranje korisnika <br />
* koristi REST web servis {korisnicko_ime}_aplikacija_1 za upavljanje IoT uređajima
