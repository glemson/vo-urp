
set terminal png small size 400,300
set output "%output%"

set xlabel "%xlabel%"
%logScaleX%

set ylabel "%ylabel%"

plot "%input%" u 1:2 title "%title%" w l lw 2

exit