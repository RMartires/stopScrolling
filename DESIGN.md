# BrainPal Dev Design System

## Product character

BrainPal is a calm, private analytics tool. It reports behavior without shame,
diagnosis, or celebration that the data cannot support. The interface should
feel editorial and precise rather than like a generic dashboard.

## Hierarchy

1. One period-defining number or app-share composition.
2. One comparison or coverage statement.
3. One drill-down visualization and one neutral insight.

Use typography, whitespace, and dividers before adding a container. A surface
must group an interaction or a consequential status; it must not decorate a
single metric.

## Color tokens

- `Background`: `#120A1D`
- `Surface`: `#1D122B`
- `SurfaceRaised`: `#29193B`
- `Primary`: `#FF9A62`
- `Secondary`: `#BCA1F5`
- `TextPrimary`: `#FAF4FF`
- `TextSecondary`: `#CDBFE0`
- `Outline`: `#554268`
- `Success`: `#71D6A0`
- `Warning`: `#FFC36B`
- `Error`: `#FF8E82`
- YouTube: `#FF5A5F`
- Instagram: `#E56A9B`
- TikTok: `#55D9D0`

Color is never the only signal. Pair app colors with names/icons and heatmap
intensity with ordered levels plus exact values.

## Typography

Use the bundled Manrope variable font.

- Display total: 64sp / ExtraBold / tabular numerals
- Screen title: 28sp / Bold
- Section title: 18sp / SemiBold
- Body: 16sp / Regular
- Supporting label: 14sp / Medium
- Chart annotation: 12sp / Medium, only when contrast remains WCAG AA

At 200% font scale, app labels and values stack. Do not scale chart marks down
to preserve a single-row layout.

## Spacing and shape

- Base spacing unit: 4dp
- Screen horizontal padding: 20dp
- Major section gap: 28dp
- Related content gap: 12dp
- Minimum touch target: 48dp
- Interactive surface radius: 16dp
- Dialog radius: 24dp
- Progress/share band radius: 8dp

Do not apply one large bubbly radius to every element. Charts normally render
directly on the page.

## Analytics states

- `Tracked`: full-opacity mark
- `TrackedZero`: outlined mark with zero label
- `Unavailable`: diagonal hatch or dashed outline plus “Unavailable”
- `Future`: no mark and not focusable
- `Selected`: 2dp high-contrast outline and a visible detail row
- `Demo`: persistent “Demo” badge and semantic origin label

## Motion

Motion communicates continuity only:

- 180ms range-content crossfade
- 220ms app-share/filter transition
- 180ms selected-detail reveal

Honor reduced-motion settings by switching to immediate state changes.

## Writing

Use neutral, descriptive language:

- “18 fewer reels than the previous 30 days.”
- “12 of 30 days recorded.”
- “Not enough history for a comparison.”

Avoid “wasted,” “bad,” “addicted,” “great job,” or causal claims.

## Accessibility

- WCAG AA contrast for all text and meaningful marks.
- Every chart has a concise summary and focusable marks with exact values.
- Focus order follows visible reading order.
- Values never depend on color or position alone.
- Phone-first layout uses a capped 600dp content width on larger displays.
