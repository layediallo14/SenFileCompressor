"##SenFileCompressor" 
L’objectif de ce MP est de mettre sur place un logiciel d’archivage et de compression de
fichiers.
Pour rappel, l’archivage est l’utilisation d'un seul fichier pour stocker plusieurs fichiers et la
compression de données est la diminution de l'espace occupé sur le support numérique, sans
perte de qualité. On peut donc le comparer à la combinaison de tar (archivage) et gzip
(compression) dans le cadre d'une archive compressée .tgz.
1
Le travail peut donc être découpé en deux parties :
- Archivage des données : Ce module de votre programme devra permettre de regrouper
plusieurs fichiers en un seul et de pouvoir aussi, à partir du fichier regroupant le tout,
récupérer les fichiers d'origine. L’utilisation d’une bibliothèque d’archivage est interdite
à ce niveau.
- Compression des données : Ce module aura pour objectif de compresser les données
afin de réduire la taille du fichier d’archive. Il s'agira ici de faire de sorte que la taille du
fichier regroupant le tout soit le plus petit possible. A ce niveau, vous allez utiliser un
algorithme de compression sans perte bien déterminé. L'un des plus célèbres est
DEFLATE (qui est d'ailleurs utilisé par le format Zip et dont une implémentation est
disponible en Java) mais rien ne vous oblige à l'utiliser, il en existe d'autres après tout.
