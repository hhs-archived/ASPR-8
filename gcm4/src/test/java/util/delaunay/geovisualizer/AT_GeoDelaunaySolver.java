package util.delaunay.geovisualizer;

import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.annotations.UnitTag;
import tools.annotations.UnitTestMethod;
import util.delaunay.GeoDelaunaySolver;

public class AT_GeoDelaunaySolver {

	@Test
	@UnitTestMethod(target = GeoDelaunaySolver.class, name = "solve", args = { Map.class }, tags = { UnitTag.MANUAL })
	public void testSolve() {
		// Should be manually tested. See GeoVisualzerFrame for a manual
		// demonstration that displays points on a 3D globe.
	}
}
