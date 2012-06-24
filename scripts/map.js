window.Karte = (function () {
	
	var controller = {
		uiElements : {
			toolbar : null,
			searchButton: null,
			exportButton: null,
			importButton: null,
			saveButton: null,
			searchField: null
		},
		init : function () {
			// UI-Elemente mit Referenzen versehen
			this.uiElements.toolbar = document.getElementById("toolbar");
			this.uiElements.searchButton = document.querySelector("button[title='Suchen']");
			this.uiElements.exportButton = document.querySelector("button[title='Exportieren']");
			this.uiElements.importButton = document.querySelector("button[title='Importieren']");
			this.uiElements.saveButton = document.querySelector("button[title='Sichern']");
			this.uiElements.searchField = document.getElementById("searchField");
			
			// EventListener hinzufügen
			this.uiElements.searchButton.addEventListener("click", this.handler.enableSearch, false);
			this.uiElements.exportButton.addEventListener("click", function (e) { console.log(e); }, false);
			this.uiElements.importButton.addEventListener("click", function (e) { console.log(e); }, false);
			this.uiElements.saveButton.addEventListener("click", function (e) { console.log(e); }, false);
			
			this.uiElements.searchField.addEventListener("keyup", controller.handler.handleSearchInput, false);
			this.uiElements.searchField.addEventListener("keydown", controller.handler.handleSearchInput, false);
		},
		handler : {
			enableSearch : function (event) {
				// Suchfeld einblenden und fokussieren
				controller.uiElements.toolbar.className = "searchEnabled";
				controller.uiElements.searchField.focus();
				
				controller.uiElements.searchButton.removeEventListener("click", controller.handler.enableSearch, false);
			},
			handleSearchInput : function (event) {
				if (event.type === "keydown" && event.keyCode === 27) {
					if (controller.uiElements.searchField.value === "") {
						controller.handler.disableSearch();
					}
				} else if (event.type === "keyup" && event.keyCode === 13) {
					controller.handler.performSearch();
				}
			},
			performSearch : function (event) {
				alert("Du suchst also nach \"" + controller.uiElements.searchField.value + "\". Wenn jetzt die Suchfunktion funktionieren würde...");
			},
			disableSearch : function () {
				controller.uiElements.toolbar.className = "";
				controller.uiElements.searchButton.addEventListener("click", controller.handler.enableSearch, false);
			}
		}
	};
	
	return {
		init: function () {
			controller.init();
		}
	}
	
})();

window.addEventListener("DOMContentLoaded", function() {
	Karte.init();
}, false);