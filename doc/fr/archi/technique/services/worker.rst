Worker
######

Type :
	Composant VITAM Java

Données gérées :
	* Aucune

Typologie de consommation de resources :
	* CPU : fort
	* Mémoire : fort
	* Réseau : fort (entrant et sortant)
	* Disque : faible (logs)

.. todo à confirmer l'usage de disque faible (cache local des fichiers de travail ?)

.. seealso:: Ce composant fait également appel :doc:`au composant Siegfried <Siegfried>` pour l'identification des formats de fichier.

Particularités
==============

Les workers utilisent des outils externes pouvant avoir des pré-requis importants sur les OS utilisés ; pour réduire l'impact sur les systèmes, ces outils pourront être à terme packagés dans des conteneurs Docker. Cependant, aucun conteneur Docker n'est fourni ni supporté dans cette version de la solution VITAM.
