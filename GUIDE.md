# Guide de Compilation et Déploiement du Framework

Ce document résume la boucle de travail exacte à effectuer à chaque fois que le code source du Framework est modifié, afin que ces modifications soient prises en compte dans le projet `testApplication`.

---

## Phase 1 : Côté Framework (Fabrication de l'outil)

Dans un **premier terminal**, exécuter les commandes suivantes pour compiler le Framework et générer la nouvelle bibliothèque (`.jar`) :

```powershell
# 1. Se placer dans le dossier du Framework
cd "D:\WEB DYN\FRAMEWORK_fra\Framework"

# 2. Recompiler toutes les classes
./run1.bat

# 3. Se placer dans le dossier des classes fraîchement compilées
cd build\WEB-INF\classes

# 4. Créer le nouveau fichier .jar
jar -cvf essai.jar *

copier le fichier .jar generer dans Framework/buid 
coller dans TestApplication /lib 

./deploy.bat