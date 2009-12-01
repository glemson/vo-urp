
#pname1 = "/afs/mpa/data/eyal/Projects/effective_sam/Results/Models/bursty/" ; # folder of sesam results
pname1=""
#pname2 = "/afs/mpa/data/eyal/Projects/effective_sam/public/plots_input/" ;  # folder of input
pname2="/store2/Legacy/Apps/SeSAM/gnuplot/input/"

###########################################################
####   Plot galaxy stellar mass function at z=0     #######
###########################################################

reset
f1 = pname1."gsmf0" ;
f2 = pname2."lowz_mf.dat" ;
unset title ;

set title "Stellar mass function at z=0"


set xlabel "Log[ m_s/M_{sun} ]"
se xtics nomirror
set ylabel "\Phi / Mpc^{-3} dex^{-1} "
set yrange [3e-6:1e-1]
set xrange [9:12]
set logscale y



set terminal png
#set output "gsmf0.png"
set output "fig2_1.png"

plot f1 using 1:($$2/0.1) title 'SESAM' linewidth 4 with lines,\
f2 using 1:2 linewidth 4 title 'Li & White (2009)', \
f2 using 1:3 linewidth 4 title 'Baldry et al (2008)', \
f2 using 1:4 linewidth 4 title 'Panter et al (2007)'

set output 


###########################################################
####   Plot galaxy stellar mass function at z=1     #######
###########################################################

reset
f1 = pname1."gsmf5" ;
f2 = pname2."highz_mf.dat" ;
unset title ;

set title "Stellar mass function at 0.8<z<1.2"


set xlabel "Log[ m_s/M_{sun} ]"
se xtics nomirror
set ylabel "\Phi / Mpc^{-3} dex^{-1} "
set yrange [3e-6:1e-1]
set xrange [9:12]
set logscale y


set terminal png
set output "fig2_2.png"


# [phi_o{2,1}.m, phi_o{2,1}.mf, z1, z2] = highz_SMF( 3, 2, pname_in ) ; % Bundy, z=0.75-1
# [phi_o{2,2}.m, phi_o{2,2}.mf, z1, z2] = highz_SMF( 1, 4, pname_in ) ; % Borch, z=0.8-1 
# [phi_o{2,3}.m, phi_o{2,3}.mf, z1, z2] = highz_SMF( 2, 5, pname_in ) ; % Perez-Gonzalez , z=0.8-1
# [phi_o{2,4}.m, phi_o{2,4}.mf, z1, z2] = highz_SMF( 7, 3, pname_in ) ; % Fontana, z=0.8-1
# [phi_o{2,5}.m, phi_o{2,5}.mf, z1, z2] = highz_SMF( 4, 3, pname_in ) ; % Drory04, z=0.8-1
# [phi_o{2,6}.m, phi_o{2,6}.mf, z1, z2] = highz_SMF( 5, 2, pname_in ) ; % Drory05a, z=0.75-1.25
# [phi_o{2,7}.m, phi_o{2,7}.mf, z1, z2] = highz_SMF( 6, 2, pname_in ) ; % Drory05b, z=0.75-1.25

plot f1 using 1:($$2/0.1) title 'SESAM' linewidth 4 with lines,\
f2 using  17:(10**($$18))  linewidth 4 title 'Bundy, z=0.75-1', \
f2 using  19:(10**($$20))  linewidth 4 title 'Borch, z=0.8-1', \
f2 using  21:(10**($$22))  linewidth 4 title 'Perez-Gonzalez , z=0.8-1', \
f2 using  23:(10**($$24))  linewidth 4 title 'Fontana, z=0.8-1', \
f2 using  25:(10**($$26))  linewidth 4 title 'Drory04, z=0.8-1', \
f2 using  27:(10**($$28))  linewidth 4 title 'Drory05a, z=0.75-1.25' , \
f2 using  29:(10**($$30))  linewidth 4 title 'Drory05b, z=0.75-1.25' 

set output 


###########################################################
####   Plot galaxy stellar mass function at z=1.5     #######
###########################################################

reset
f1 = pname1."gsmf7" ;
f2 = pname2."highz_mf.dat" ;
unset title ;

set title "Stellar mass function at 1.2<z<2"


set xlabel "Log[ m_s/M_{sun} ]"
se xtics nomirror
set ylabel "\Phi / Mpc^{-3} dex^{-1} "
set yrange [3e-6:1e-1]
set xrange [9:12]
set logscale y


set terminal png
set output "fig2_3.png"

# [phi_o{3,8}.m, phi_o{3,8}.mf, z1, z2] = highz_SMF( 8, 1, input_dir ) ; % Marchesini, z=1.3-2
# [phi_o{3,3}.m, phi_o{3,3}.mf, z1, z2] = highz_SMF( 2, 8, input_dir ) ; % Perez-Gonzalez , z=1.6-2
# [phi_o{3,4}.m, phi_o{3,4}.mf, z1, z2] = highz_SMF( 7, 6, input_dir ) ; % Fontana, z=1.6-2
# [phi_o{3,6}.m, phi_o{3,6}.mf, z1, z2] = highz_SMF( 5, 4, input_dir ) ; % Drory05a, z=1.75-2.25
# [phi_o{3,7}.m, phi_o{3,7}.mf, z1, z2] = highz_SMF( 6, 4, input_dir ) ; % Drory05b, z=1.75-2.25


plot f1 using 1:($$2/0.1) title 'SESAM' linewidth 4 with lines,\
f2 using  47:(10**($$48))  linewidth 4 title 'Marchesini, z=1.3-2', \
f2 using  37:(10**($$38))  linewidth 4 title 'Perez-Gonzalez , z=1.6-2', \
f2 using  39:(10**($$40))  linewidth 4 title 'Fontana, z=1.6-2', \
f2 using  43:(10**($$44))  linewidth 4 title 'Drory05a, z=1.75-2.25', \
f2 using  45:(10**($$46))  linewidth 4 title 'Drory05b, z=1.75-2.25'

set output 


###########################################################
####   Plot galaxy stellar mass function at z=2.5     #######
###########################################################

reset
f1 = pname1."gsmf9" ;
f2 = pname2."highz_mf.dat" ;
unset title ;

set title "Stellar mass function at 2<z<3"


set xlabel "Log[ m_s/M_{sun} ]"
se xtics nomirror
set ylabel "\Phi / Mpc^{-3} dex^{-1} "
set yrange [3e-6:1e-1]
set xrange [9:12]
set logscale y


set terminal png
set output "fig2_4.png"

# [phi_o{4,4}.m, phi_o{4,4}.mf, z1, z2] = highz_SMF( 7, 7, pname_in ) ; % Fontana, z=2-3
# [phi_o{4,8}.m, phi_o{4,8}.mf, z1, z2] = highz_SMF( 8, 2, pname_in ) ; % Marchesini, z=2-3
# [phi_o{4,6}.m, phi_o{4,6}.mf, z1, z2] = highz_SMF( 5, 5, pname_in ) ; % Drory05a, z=2.25-3
# [phi_o{4,7}.m, phi_o{4,7}.mf, z1, z2] = highz_SMF( 6, 5, pname_in ) ; % Drory05b, z=2.25-3
# [phi_o{4,3}.m, phi_o{4,3}.mf, z1, z2] = highz_SMF( 2, 10, pname_in ) ; % Perez-Gonzalez , z=2.5-3

plot f1 using 1:($$2/0.1) title 'SESAM' linewidth 4 with lines,\
f2 using  55:(10**($$56))  linewidth 4 title 'Fontana, z=2-3', \
f2 using  63:(10**($$64))  linewidth 4 title 'Marchesini, z=2-3', \
f2 using  59:(10**($$60))  linewidth 4 title 'Drory05a, z=2.25-3', \
f2 using  61:(10**($$62))  linewidth 4 title 'Drory05b, z=2.25-3', \
f2 using  53:(10**($$54))  linewidth 4 title 'Perez-Gonzalez , z=2.5-3'

set output 

