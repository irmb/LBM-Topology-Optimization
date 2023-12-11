# Legacy LBM Topology Optimization

This Java applet imlements a heuristic topology optimization strategy for the minimzation of dissipation in fluids. The code was originally written to run in a webbrowser and has been slightly modified to run as a stand alone. It has otherwise not been updated size 2007 and is no longer under active development.



## How to cite
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