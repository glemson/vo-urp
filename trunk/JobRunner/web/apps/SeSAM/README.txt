 
This README file contains information regarding the output files of
the SESAM model. For more details on the model please refer to the
original paper, Neistein & Weinmann (2009): http://arxiv.org/abs/0911.3147


List of output files
-----------------------
- "catalog_z" : specifies the redshift of each catalog
- "catalog0.0", 'catalog1.0", ... "catalog13.0" :
each file includes information on galaxies at a given redshift.


File structure: "catalog_z"
------------------------------
The first line of the file is a string comment, starting with "#"

Column description:
1. Catalog index, an integer between 0 and 13, refers to the integer
in the catalog file name. For example, "11" refers to file "catalog11.0"
2. The snapshot number of this output within the Millennium simulation
3. The redshift of this catalog file


File structure: "catalog0.0"
---------------------------------
The first line of the file is a string comment, starting with "#"
Column description:

1. index: Running index within the catalog file

2. FoF-mass:
The mass of the FoF group which includes this galaxy. This is "m_Crit200" from the
MPAHalo database of the Millennium simulation. It is defined as the mass within the
radius where the halo has an overdensity 200 times the critical density of the simulation.
Units 1010 M_sun/h

3. Subhalomass:
The mass of the host subhalo, corresponding to the number of simulation particles
included in this subhalo.
Units 1010 M_sun/h

4. hot_gas
Mass of hot gas sorrounding this galaxy
Units of 1010 M_sun/h

5. cold_gas
Mass of cold gas within this galaxy
Units of 1010 M_sun/h

6. stars
Mass of stars within this galaxy
Units of 1010 M_sun/h

7. sfr
Star formation rate, averaged over the last simulation snapshot (~250 Myr)
Units of M_sun/yr

8. mean_time
Average formation time for stars in this galaxy
Units of Gyr since the big-bang

9. gal_type
0 - central galaxy inside the most massive subhalo of the FoF group
1 - central galaxy inside a subhalo which is *not* the most massive in its FoF group
2 - a galaxy which is a satellite inside a subhalo
3 - a galaxy which at high-z didn't have any descendant subhalo, it is followed until z=0

10. halo_index
Index of the merger-tree within the full run

11-13. x, y, z
Galaxy location. If this is a type 0/1/3 galaxy, then location is the same
as the location of the host subhalo. For type-2 galaxies location is estimated
by linear interpolation between two points: the position where the last host
subhalo was defined (and the galaxy was type 1/0), and the position of the current
central subhalo. Interpolation is done according to dynamical friction time estimate.
Units: Mpc/h
