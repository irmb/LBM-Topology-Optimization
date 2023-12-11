<!--
SPDX-FileCopyrightText: 2023 Martin Geier <mailto:geier(at)irmb.tu-bs.de>
SPDX-License-Identifier: CC-BY-4.0
-->
# LBM Topology Optimization
[![Project Status: Inactive – The project has reached a stable, usable state but is no longer being actively developed; support/maintenance will be provided as time allows.](https://www.repostatus.org/badges/latest/inactive.svg)](https://www.repostatus.org/#inactive)
 [![pipeline status](https://git.rz.tu-bs.de/irmb/lbm-topology-optimization/badges/main/pipeline.svg)](https://git.rz.tu-bs.de/irmb/lbm-topology-optimization/-/commits/main)
 [![Latest Release](https://git.rz.tu-bs.de/irmb/lbm-topology-optimization/-/badges/release.svg)](https://git.rz.tu-bs.de/irmb/lbm-topology-optimization/-/releases) [![REUSE status](https://api.reuse.software/badge/git.rz.tu-bs.de/irmb/lbm-topology-optimization)](https://api.reuse.software/info/git.rz.tu-bs.de/irmb/legacy-lbm-topology-optimization)


This Java applet imlements a heuristic topology optimization strategy for the minimzation of dissipation in fluids. The code was originally written to run in a webbrowser and has been slightly modified to run as a stand alone. It has otherwise not been updated size 2007 and is no longer under active development.

## Download
The continuous integration pipeline builds the code and creates an executable Jar for each commit on the `main` branch. The latest jar can be downloaded [here](https://git.rz.tu-bs.de/api/v4/projects/4898/jobs/artifacts/main/download?job=compile_java).

Alternatively, the source code can be downloaded as a zip file from the [latest release](https://git.rz.tu-bs.de/irmb/legacy-lbm-topology-optimization/-/releases).


## How to cite
The software is published on [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10354082.svg)](https://doi.org/10.5281/zenodo.10354082) and can be cited:
```
@software{software,
  author       = {Geier, Martin and
                  Liu, Zhenyu},
  title        = {LBM Topology Optimization},
  month        = dec,
  year         = 2023,
  publisher    = {Zenodo},
  version      = {1.0.0},
  doi          = {10.5281/zenodo.10354082},
  url          = {https://doi.org/10.5281/zenodo.10354082}
}
```

A detailed description is found in: **Cellula automaton based fluidic topology optimization**, M. Geier, Z. Liu, A. Greiner and J.G. Korvink in "Recent Developments in Structural Engineering, Mechanics and Computation", 2007 Millpress Rotterdam ISBN 9789059660540.

```
@article{article,
    author = {Geier, Martin and Liu, Zhenyu and Greiner, A. and Korvink, Jan},
    year = {2007},
    month = {09},
    pages = {},
    title = {Cellular automaton based fluidic topology optimization},
    journal = {Third International Conference on Structural Engineering, Mechanics and Computation (SEMC 2007)}
}
```


## Acknowledgements
Grateful for the assistance provided by Peng Liu from Changchun, China, in transforming the JApplet application into a desktop application by inheriting from the JFrame class.
The specific tasks included performing the initialization work of the original JApplet framework in the new constructor and replacing the original browser status bar with a JLabel.

Thanks to Soeren Peters, who implemented the Continuous Integration pipeline, made the project REUSE compliant, and managed the software publication to Zenodo.
