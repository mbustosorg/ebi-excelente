EBI Excelente Donor Appreciation Application
==========================

This is an application that can be used to drive a Processing program
to display donor messages for a school fund raising auction.

This donor input portion of the applciation is currently deployed at
https://ebi-excelente.herokuapp.com

The Processing portion of the application was developed using
Processing 2.x.

To run it for your own deployment you will need to set the following ENV variables:
* EBI_MYSQL_URL
* EBI_MYSQL_USER
* EBI_MYSQL_PASSWORD

To set these in Heroku, you can do the following:

```bash
$ heroku config:set EBI_MYSQL_URL=jdbc:mysql://mysql.*****:3306/ebiexcelente
Setting config vars and restarting ebi-excelente... done
EBI_MYSQL_URL: jdbc:mysql://*****:3306/ebiexcelente
$ heroku config:set EBI_MYSQL_USER=ebiexcelenteuser
Setting config vars and restarting ebi-excelente... done
EBI_MYSQL_USER: ebiexcelenteuser
$ heroku config:set EBI_MYSQL_PASSWORD=*****
Setting config vars and restarting ebi-excelente... done
EBI_MYSQL_PASSWORD: *****
```
