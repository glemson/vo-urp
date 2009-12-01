
#pname1 = "/afs/mpa/data/eyal/Projects/effective_sam/Results/Models/bursty/" ; # folder of sesam results
pname1=""
#pname2 = "/afs/mpa/data/eyal/Projects/effective_sam/public/plots_input/" ;  # folder of input
pname2="/store2/Legacy/Apps/SeSAM/gnuplot/input/"

###########################################################
####            Plot the Madau diagram              #######
###########################################################

reset
f1 = pname1."madau_diagram" ;
f2 = pname2."madau_hopkins.dat" ;
f3 = pname2."madau_wilkins.dat" ;
unset title ;

set title "The Universal SF rate density"


set xlabel "Redshift"
se xtics nomirror
set ylabel "d(rho_s)/dt  [ M_{sun} /yr/ Mpc^3 ]"
set yrange [5e-3:0.5]
set xrange [0:4]
set logscale y



set terminal png
#set output "madau.png"
set output "fig3.png"

plot f1 using 1:2 title 'SESAM' linewidth 4 with lines,\
f2 using 1:2 linewidth 2 title 'Hopkins & Beacom (2006) - Min', \
f2 using 1:3 linewidth 2 title 'Hopkins & Beacom (2006) - Max', \
f3 using 1:(10**($$2)) linewidth 2 title  'Wilkins et al (2008) - Max', \
f3 using 3:(10**($$4)) linewidth 2 title 'Wilkins et al (2008) - Min'


set output 


