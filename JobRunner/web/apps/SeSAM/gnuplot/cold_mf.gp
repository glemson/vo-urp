
#pname1 = "/afs/mpa/data/eyal/Projects/effective_sam/Results/Models/bursty/" ; # folder of sesam results
pname1=""
#pname2 = "/afs/mpa/data/eyal/Projects/effective_sam/public/plots_input/" ;  # folder of input
pname2="/store2/Legacy/Apps/SeSAM/gnuplot/input/"

###########################################################
####            Plot cold gas mass functions        #######
###########################################################

reset
f1 = pname1."gas_mass_fun" ;
f2 = pname2."gas_mf.dat" ;
unset title ;

set title "Cold gas mass function, z=0"


set xlabel "Log[ m_{cold}/M_{sun} ]"
se xtics nomirror
set ylabel "Log(\Phi)    [ Mpc^{-3} dex^{-1} ]"
set yrange [-6:0]
set xrange [8:11.5]
# set logscale y
# set logscale x


set terminal png size 400,300
#set output "gas_mf.png"
set output "fig1.png"


plot f1 using 1:(log10($$2/0.1+1e-6)) title 'SESAM' linewidth 1, \
f2 using 1:(log10($$3)) linewidth 2 lt 2 with lines title 'Zwaan et al. (2005)', \
f2 using 1:(log10($$2)) linewidth 2 lt 3 with lines title 'Obreschkow & Rawlings (2009)'



set output 


