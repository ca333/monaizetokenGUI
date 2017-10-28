# [MonaizeToken](https://monaize.com/) Desktop GUI Wallet - for Mac OS X
[disclaimer](#disclaimer)

MonaizeToken GUI for Mac OS is not yet officially supported. This experimental build has been provided by [ca333](https://github.com/ca333).
In case of any problems you may contact the developer for help.
You can get the prebuilt OSX binaries (mnzd/mnz-cli) [here](https://github.com/Monaize/monaizetoken/releases/).

![Screenshot](https://github.com/ca333/monaizetokenGUI/raw/master/docs/mnzgui.jpg "MNZ GUI on Mac")

1. Build tools

   You need to install git, JDK 8 and Ant for Mac OS to build the GUI wallet. The commands
   `git`, `java`, `javac` and `ant` need to be startable from command line before proceeding with
   build. The procedure could be:

   1.1. [Install homebrew](http://brew.sh/)

   1.2. Install git: `brew install git`

   1.3. [Install JDK 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html)

   1.4. [Install Ant](http://www.admfactory.com/how-to-install-apache-ant-on-mac-os-x/)

2. Building the MNZ GUI wallet

   Version 0.8 (beta) or later needs to be built from source. The build procedure is the same as on Linux.
   Summary of commands:
   ```
   git clone https://github.com/ca333/monaizetokenGUI.git
   cd monaizetokenGUI
   ant -buildfile ./src/build/build.xml
   chmod u+x ./build/jars/MonaizeWalletUI.jar
   ```
   At this point the build process is finished the built GUI wallet program is the JAR
   file `./build/jars/MonaizeWalletUI.jar`

### License
This program is distributed under an [MIT License](https://github.com/ca333/monaizetokenGUI/raw/master/LICENSE).

### Disclaimer
This program is not officially endorsed by or associated with the ZCash project and the ZCash company.
[ZCash®](https://trademarks.justia.com/871/93/zcash-87193130.html) and the
[ZCash® logo](https://trademarks.justia.com/868/84/z-86884549.html) are trademarks of the
[Zerocoin Electric Coin Company](https://trademarks.justia.com/owners/zerocoin-electric-coin-company-3232749/).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

### Known issues and limitations

1. Limitation: Wallet encryption has been temporarily disabled in ZCash (which is the base of monaizetoken) due to stability problems. A corresponding issue
[#1552](https://github.com/zcash/zcash/issues/1552) has been opened by the ZCash developers. Correspondingly
wallet encryption has been temporarily disabled in the MNZ Desktop GUI Wallet.
1. Issue: the GUI wallet does not work correctly if mnzd is started with a custom data directory, like:
`mnzd -datadir=/home/data/whatever` This will be fixed in later versions.
1. Issue: GUI data tables (transactions/addresses etc.) allow copying of data via double click but also allow editing.
The latter needs to be disabled.
1. Limitation: The list of transactions does not show all outgoing ones (specifically outgoing Z address
transactions). A corresponding issue [#1438](https://github.com/zcash/zcash/issues/1438) has been opened
for the ZCash developers.
1. Limitation: The CPU percentage shown to be taken by komodod on Linux is the average for the entire lifetime
of the process. This is not very useful. This will be improved in future versions.
