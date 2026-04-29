# Design System Document: La Farmacia Doldom Interface

## 1. Overview & Creative North Star: "The Digital Pharmacy"
This design system moves away from the sterile, cold efficiency of traditional medical software. Our Creative North Star is **"The Digital Pharmacy"**—a space that feels as tactile and reassuring as a high-end wellness boutique. 

The system rejects the "standard dashboard" look. Instead of rigid boxes and harsh lines, we use **Organic Layering**. We achieve a premium, editorial feel by prioritizing expansive white space (or in our case, "Cream Space"), intentional asymmetry in layout, and a sophisticated typographic hierarchy that guides the pharmacist’s eye with the grace of a printed journal.

## 2. Colors: Tonal Healing
Our palette is rooted in nature and comfort. We avoid "digital pure" colors in favor of "organic muted" tones.

### Surface Hierarchy & The "No-Line" Rule
**The Golden Rule:** 1px solid borders are strictly prohibited for sectioning. 
Boundaries are defined through **Background Color Shifts**. To separate the navigation from the workspace, do not draw a line; instead, place a `surface-container-low` sidebar against a `surface` main stage.

*   **Surface Nesting:** Treat the UI as physical layers of fine paper. 
    *   **Base Layer:** `surface` (#feffd5) - The foundation of the application.
    *   **Sectional Layer:** `surface-container-low` (#fcfaed) - For secondary navigation or grouping.
    *   **Prominent Layer:** `surface-container-highest` (#ebe9d8) - For active utility panels or modal backdrops.
*   **The Glass & Gradient Rule:** For main CTAs and "Hero" cards, use a subtle linear gradient from `primary` (#21733d) to `primary-container` (#a3f5b2) at a 135-degree angle. This adds a "soul" to the UI that flat color cannot replicate. For floating overlays, use **Glassmorphism**: a semi-transparent `surface-container-lowest` with a 20px backdrop-blur.

## 3. Typography: Editorial Authority
We utilize two distinct sans-serifs to balance technical precision with approachable warmth.

*   **Display & Headlines (Manrope):** This is our "Editorial" voice. Use `display-lg` to `headline-sm` for page titles and high-level summaries. Its geometric yet soft curves convey modern authority.
*   **Body & Labels (Plus Jakarta Sans):** This is our "Utility" voice. Chosen for its exceptional legibility at small sizes. Use `body-md` for patient notes and `label-md` for technical data like dosages.

**Hierarchy Strategy:** 
Use `primary` (#21733d) for headlines to establish trust, and `on-surface-variant` (#656558) for secondary body text to reduce eye strain during long shifts.

## 4. Elevation & Depth: Tonal Layering
In this system, "Elevation" is a feeling, not just a shadow.

*   **The Layering Principle:** Depth is achieved by stacking. Place a `surface-container-lowest` (#ffffff) card on a `surface-container` (#f6f4e6) background. This creates a "soft lift" that feels architectural rather than digital.
*   **Ambient Shadows:** If a card must float (e.g., a prescription preview), use a "Bespoke Glow" shadow:
    *   `X: 0, Y: 8px, Blur: 32px, Spread: -4px`
    *   **Color:** Use `on-surface` (#38392d) at 6% opacity. Never use pure black.
*   **The Ghost Border Fallback:** If accessibility requirements demand a container edge, use the **Ghost Border**: `outline-variant` (#bbbaaa) at 15% opacity. It should be felt, not seen.

## 5. Components: Soft Utility

### Buttons
*   **Primary:** Gradient fill (`primary` to `primary-dim`), `xl` (1.5rem) rounded corners. Text: `title-sm` in `on-primary`.
*   **Secondary:** `secondary-container` (#ffdcc5) background with `on-secondary-container` (#814000) text. No border.
*   **Tertiary:** No background. Use `primary` text with a `surface-container-highest` hover state.

### Input Fields
*   **Base:** `surface-container-lowest` (#ffffff) fill.
*   **Corners:** `md` (0.75rem).
*   **States:** On focus, transition the background to `primary-container` at 20% opacity and use a 2px `primary` "Ghost Border" (20% opacity).

### Cards & Lists (The Divider-Free Approach)
*   **Strategy:** Forbid 1px dividers.
*   **Implementation:** Separate list items using a 12px vertical gap (`Spacing Scale`). To highlight a selected item, change its background to `surface-container-high` and apply an `lg` (1rem) corner radius.

### Specialized Pharmacy Components
*   **Dosage Chips:** Use `secondary-fixed` (#ffdcc5) for "Warning" or "Urgent" meds and `tertiary-fixed` (#c2f1ce) for "Maintenance" meds. Shape: `full` pill-round.
*   **Prescription Timeline:** A vertical track using `outline-variant` at 20% opacity, with `primary` nodes.

## 6. Do's and Don'ts

### Do:
*   **Do** use `surface-dim` for inactive or "sunken" utility areas.
*   **Do** use asymmetrical margins (e.g., wider left margins for headlines) to create an editorial, non-templated look.
*   **Do** prioritize `primary-fixed-dim` (#95e7a5) for success states instead of harsh "neon" greens.

### Don't:
*   **Don't** use 100% black (#000000) anywhere. Use `on-surface` (#38392d) for maximum eye comfort.
*   **Don't** use `none` or `sm` roundedness. This system requires `md` (0.75rem) as the absolute minimum to maintain its "friendly" DNA.
*   **Don't** use high-contrast transitions. All state changes (hover, focus) should animate over 300ms using a `cubic-bezier(0.4, 0, 0.2, 1)` curve for a "living" feel.