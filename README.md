### KIT SOC4S Projekt: BlueJ Klasseneditor Prototyp

Bei dem Projekt handelt es sich um eine einfache Implementierung eines UML-Klassendiagramm-Editors.
Das Projekt ist als BlueJ Erweiterung umgesetzt, kann aber für weitere Einsatzszenarien 
leicht erweitert werden. Die modellierten Elemente können dann in Java-Code transformiert werden.
Der Editor unterstützt auch die Erstellung eines Diagramms aus bestehenden Java-Code.

## Installation
Die Installation einer BlueJ Extension ist unkompliziert und wird auf 
https://www.bluej.org/extensions/extensions.html beschrieben.

Die Erweiterung kann in der Releases-Sektion runtergeladen werden. Es wird nur die Jar-Datei
benötigt. Diese muss in den entsprechenden Ordner für BlueJ Erweiterungen kopiert werden.
Damit ist die Installation vollständig und die Erweiterung kann in BlueJ verwendet werden.

## Benutzung
Die Erweiterung kann in BlueJ direkt gestartet werden. Dazu befindet sich im Menüpunkt "Werkzeuge"
in BlueJ eine Auswahlmöglichkeit "Klasseneditor öffnen".
Dadurch wird ein neues Fenster mit dem Klasseneditor gestartet. Die Erweiterung unterstützt zurzeit
nur Projekte mit einem Package. Es wird standardmäßig immer das erste verwendet, wobei dir Reihenfolge
von BlueJ bestimmt ist und nicht weiter definiert ist.

Die Benutzeroberfläche unterteilt sich in drei Hauptbereiche. Den größten Teil nimmt die
Zeichenfläche für das Diagramm ein. Hier können hinzugefügte Elemente verschoben und ausgerichtet
werden. Ausgehend von Verbindungspunkten, können Beziehungen zwischen Elementen hergestellt werden.
Standardmäßig werden folgende Beziehungen erstellt:

- Generalisierung: Klasse --> Klasse
- Spezialisierung: Klasse --> Interface
- Erweiterung: Interface --> Interface
- Assoziation: Interface --> Klasse 

Soll eine Assoziation gezeichnet werden, befindet sich eine Checkbox in der oberen Leiste mit
"Assoziation zeichnen". Ist diese Option ausgewählt, wird die Standardbelegung ignoriert.
Beziehungen zwischen Elementen können nur in der Diagrammansicht gelöscht werden. Dazu muss die
Beziehung durch Klicken ausgewählt werden (angezeigt durch einen blauen Rahmen). Mit der
Entfernen-Taste kann die Beziehung dann gelöscht werden.

Mit einem Doppelklick auf Diagrammelemente öffnet sich ein Dialog zum Bearbeiten dieses Elements.
Diese Funktionalität gilt auch für die Liste am linken Bildschirmrand. In dieser Liste können
zusätzlich Elemente ein- bzw. ausgeblendet werden.

Soll ein Element hinzugefügt werden, so ist dies über den Button "Element hinzufügen" oben links
möglich.

Oben rechts befinden sich die Kontrollflächen zum Laden und Speichern des Diagramms.
Beim Laden wird der aktuelle Code im Package analysiert und ausgehend davon ein neues Diagramm
generiert. Das bestehende wird dabei unwiderruflich gelöscht.
"Speichern" bietet die inverse Operation zum Laden. Das aktuelle Diagramm wird in Java-Code
transformiert und im Package gespeichert. Bestehender Code bleibt dabei weitestgehend erhalten
(Bugs sind leider nicht ausgeschlossen, weitere Informationen sind in "Bekannte Probleme").
"Auswahl Speichern" erlaubt das selektive Generieren von Java-Code. Elemente können per
Mausklick im Diagramm ausgewählt werden (angezeigt durch einen blauen Rahmen).

Gelöschte Klassen im Diagramm werden bei der Codegenerierung nicht im Package gelöscht. Diese
Operation muss manuell erfolgen.

Damit eventuelle Fehler in der Codegenerierung nicht zu stark in Gewicht fallen, gibt es die
Möglichkeit, bei jeder Speicheroperation ein Backup zu erstellen. Dieses Backup besteht aus
allen Dateien, die sich zu diesem Zeitpunkt im Package befinden. Es wird im gleichen Ordner
wie das Package abgelegt mit einem Suffix "-backup".

## Bekannte Probleme

- Wenn mehr als eine Beziehung zu einem Element führen, werden sie durch den Layout-Algorithmus
übereinander gerendert.
    - Workaround: Elemente an die gewünschten Positionen ziehen und betroffene Verbindungen
    per Hand neuzeichnen.
- Getter und Setter werden mit Standardimplementierungen generiert. Eigene Veränderungen werden
überschrieben.
- Die Formatierung des generierten Codes ist nicht immer perfekt. Es kommt zu unnötigen
Zeilenumbrüchen.
- Klassenkommentare werden bei der Generierung verworfen. Methodenkommentare und Kommentare
innerhalb von Methoden bleiben aber erhalten.
- Mithilfe der Bearbeitungsfunktion bei Methoden können mehrere Methoden mit einer gleichen
Signatur erstellt werden.
- Inhalt von Elementen kann im Diagramm außerhalb der Elementgrenzen gerendert werden
    - Workaround: Elemente entsprechend in ihrer Größe anpassen
- Aufgrund der Positionen der Verbindungspunkte können Elemente nicht auf einer Ebene angeordnet
werden ohne, dass die Pfeile schräg gezeichnet werden.
- Assoziationen unterstützen keine Kardinalitäten und können nur in eine Richtung gerichtet sein.