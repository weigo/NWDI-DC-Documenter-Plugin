function search() {
	var searchField = document.getElementById('search');
	var regex = new RegExp('.*' + searchField.value + '.*');
	var compartmentNodes = document.evaluate(
			"/html/body//div[@class='compartment']", document, null,
			XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);

	for (var i = 0; i < compartmentNodes.snapshotLength; i++) {
		var compartmentNode = compartmentNodes.snapshotItem(i);
		searchCompartmentNode(compartmentNode, regex);
	}
}

function searchCompartmentNode(compartmentNode, regex) {
	var dcNodes = document
			.evaluate("..//div[@class='developmentComponent']", compartmentNode,
					null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
	var dcNodeCount = dcNodes.snapshotLength;
	var visibleComponentCount = 0;

	for (var i = 0; i < dcNodeCount; i++) {
		var dcNode = dcNodes.snapshotItem(i);

		if (regex.exec(dcNode.textContent)) {
			dcNode.setAttribute('style', 'display: block;');
			visibleComponentCount++;
		} else {
			dcNode.setAttribute('style', 'display: none;');
		}
	}

	if (dcNodeCount > 0 && visibleComponentCount == 0) {
		compartmentNode.setAttribute('style', 'display: none;');
	} else {
		compartmentNode.setAttribute('style', 'display: block;');
	}
}
