

r<-read.csv("madau_diagram",col.names=c("redshift","SFR"),header=TRUE)
png("fig1.png")
plot(r$redshift, r$SFR, type="l",xlab="redshift",ylab="SFR")
dev.off()

r<-read.csv("cond_mass_fun",col.names=c("mgal","mhalo","nhalo","ngal"),header=TRUE)
png("cond_mass_fun.png")
i=0
xlim=c(min(r$mgal),max(r$mgal))
for(m in unique(r$mhalo))
{
  ix = which(r$mhalo == m)
  if(i ==0){
    plot(r$mgal[ix], r$ngal[ix], type="l",xlab="M_gal",ylab="#", log="xy")
  } else {
    if(i==5*floor(i/5)) {
      lines(r$mgal[ix], r$ngal[ix],col=i)
    }
  }
  i=i+1
}
dev.off()

