class=kz.zvezdochet.soap.server.Calculator
clpth='./war/WEB-INF/classes'
resourcedir='./war'
outsourcedir='./src'
outdir='./war/WEB-INF/classes'
wsgen -cp "$clpth" -wsdl -keep -r "$resourcedir" -d "$outdir" -s "$outsourcedir"  $class