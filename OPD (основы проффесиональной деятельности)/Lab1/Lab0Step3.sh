#!/usr/bin/bash
cd lab0
cat scizor4/braviary.txt scizor4/braviary.txt > chimchar4_24

chmod u+rwx vigoroth1.txt
chmod u+w growlithe1
chmod u+w growlithe1/butterfree
cp vigoroth1.txt growlithe1/butterfree
chmod u-rwx vigoroth1.txt
chmod u-w growlithe1
chmod u-w growlithe1/butterfree

ln vigoroth1.txt voltorbvigoroth
mv voltorbvigoroth scizor4

ln -s /home/studs/s413041/OPDLabs/lab0/kadabra3.txt venusaurkadabra
chmod u+w growlithe1
mv venusaurkadabra growlithe1
chmod u-w growlithe1

chmod u+w nidorina0/chimchar
chmod u+r nidorina0/cubchoo
chmod u+wx nidorina0/lileep.txt
chmod u+x nidorina0/nidorino.txt
chmod u+w growlithe1
chmod u+r growlithe1/alakazam
cp -r nidorina0 growlithe1/alakazam/
chmod u-w nidorina0/chimchar
chmod u-r nidorina0/cubchoo
chmod u-wx nidorina0/lileep.txt
chmod u-x nidorina0/nidorino.txt
chmod u-w growlithe1
chmod u-r growlithe1/alakazam

chmod u+rwx chimchar4.txt
cp chimchar4.txt scizor4/braviarychimchar.txt
chmod u-rwx chimchar4.txt

ln -s nidorina0 Copy_19

cd ..