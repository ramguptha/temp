Getting Started
===============

1. Ensure that you have a valid user with the appropriate permissions on dv2wlss-linux1.absolute.com (May need a hosts entry: 172.16.157.3):

        sudo adduser <username>
        sudo vigr # Add the new user to the src group
        sudo vigr -s # Ditto

2. OS Specific setup:
  1. Windows workstation setup:
    - Install latest version of node from the 0.8 series (NOT 10. series): e.g. http://nodejs.org/dist/v0.8.22/x64/
    - Install git: http://msysgit.github.com/ or http://windows.github.com/

  2. MacOS workstation setup:
    - TBD

3. Getting the source:

  *Read only*

        git clone git://dv2wlss-linux1.absolute.com/ui.git

  *With checkin privileges*

        git clone ssh://<userName>@dv2wlss-linux1.absolute.com/var/git/ui.git

4. Starting applications:
  - Go to root of the source checkout and run "npm install" to pull dependencies, then:
   - To run Customer Center dev proxy:
     - cd sites/cc-classic
     - node app -e dv2arch OR node app -e dv2corp3
     - Accounts: { username: qaplayerui@absolute.com, password: password }, { username: bnikolic@absolute.com, password: password }
   - To run AM Web Admin:
     - cd sites/am-web-admin
     - node app
  - navigate to http://localhost:3000 to see the apps.
  - Most of our development is done for Customer Center, but all development needs to be done with the expectation that source code will be shared in multiple apps. We are primarily library developers. To help make sure that we haven't broken anything in AM Web Admin, it is possible to run AM Web Admin in such a way that it uses the packages directory from Customer Center:
    - To start AM Web Admin using the packages directory from Customer Center: node app --pl=<Customer Center checkout directory>/client/packages --p=3001

4. Documentation:
  - We are currently using version 1.5.1 of Ember.js. The documentation is available at http://emberjs.com/.
  - ~~~~We are currently using an older version of Ember.js, and the documentation at emberjs.com no longer matches it.~~~~
  - ~~~~Valid Ember.js docs: Ember 1.0rc-pre.2 docs: http://dv2wlss-linux1.absolute.com:9292/~~~~

  - Our UI is separated into components using require.js: docs: http://requirejs.org

5. For new developers:

  - Suggest playing with AM Web Admin as a starting point for understanding the platform. Host: qaams3, Port: 3971, User: admin, Password: password. Entry point is at client/am/main.js, but also check out client/login/main.js for a simpler app. All the meat is in client/packages.

