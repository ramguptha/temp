TO RUN THE DEV SERVER:

Dev environment setup (Windows):

- Install node.js: nodejs.org
- Set NODE_PATH (actually might not need this since dependencies are locally installed instead of globally: e.g. C:\Users\dduchene\AppData\Roaming\npm\node_modules)
- Open a console and navigate to the top of the top of the source tree (ui.i3)
- npm install
- npm start

Dev environment setup (MacOS):

- Pretty much the same.

Dev environment setup (Linux): 

- We had best results installing from source:
- Get source code from http://nodejs.org/download/
- tar xfz node-v0.8.9.tar.gz; cd node-v0.8.0; make; sudo make install
- Then the console recipe above.
