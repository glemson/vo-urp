
#pname1 = "/afs/mpa/data/eyal/Projects/effective_sam/Results/Models/DeLucia/" ; # folder of sesam results
pname1=""
#pname2 = "/afs/mpa/data/eyal/Projects/effective_sam/public/plots_input/" ;  # folder of input
pname2="/store2/Legacy/Apps/SeSAM/gnuplot/input/"
###########################################################
####            Plot SSFR vs. Stellar mass        #######
###########################################################

reset
f1 = pname1."catalog0.0" ;
f2 = pname2."Schiminovich_2007a.dat" ;
unset title ;

set title "SSFR versus Stellar mass"


set xlabel "M_{sun}"
se xtics nomirror
set ylabel "SSFR    [ Gyr^{-1} ]"
set yrange [-4.5:0.5]
set xrange [9:12]
#set logscale y
# set logscale x


set terminal png
# set terminal postscript eps enhanced
#set output "ssfr.png"
set output "fig4.png"

set style line 1 lt 1 lw 1 pt 2 ps 0.7
set style line 2 lt 2 lw 2 pt 2 ps 0.7
set style line 2 lt 2 lw 2 pt 2 ps 0.7
set style line 2 lt 2 lw 2 pt 2 ps 0.7
set style line 2 lt 2 lw 2 pt 2 ps 0.7

plot f1 using (log10(($$6)*1e10/0.73+1e-4)):(log10(($$7)/($$6)/10*0.73+1e-4)) title 'SESAM' linewidth 1, \
f2 using ($$2)*($$1-2)*($$1-3)*($$1-4)/(-6):($$3+9) linewidth 2 lt 3 with lines title 'Schiminovich et al (2007)', \
f2 using ($$2)*($$1-1)*($$1-3)*($$1-4)/2:($$3+9) linewidth 2 lt 3 with lines title '', \
f2 using ($$2)*($$1-1)*($$1-2)*($$1-4)/(-2):($$3+9) linewidth 2 lt 3 with lines title '', \
f2 using ($$2)*($$1-1)*($$1-2)*($$1-3)/(-6):($$3+9) linewidth 2 lt 3 with lines title ''



set output 


