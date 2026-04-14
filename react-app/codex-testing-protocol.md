# Codex Testing Protocol

Use this protocol whenever testing the `react-app` web UI.

## Run The App

- Start the app with `npm run dev` in a background terminal session.
- Keep that dev server running while testing the UI in the browser.

## Browser Tooling

- Use Chromium MCP tools for UI testing.
- Open the local dev URL in Chromium MCP.
- Use Chromium MCP to inspect the rendered UI, interact with flows, and verify behavior.
- Take screenshots during testing so visual issues are checked against the actual rendered app.

## Mobile Testing

- Always test with the assumption that the primary user may open the app on a mobile screen.
- Use Chromium MCP viewport emulation to set a mobile-sized screen before reviewing the UI.
- Check that the layout, spacing, tiles, drawers, modals, and questionnaire flow do not look broken or awkward on mobile.

## Desktop Testing

- Also test on a regular desktop viewport.
- On desktop, verify that the app does not feel stretched, oversized, or too empty across the full screen.
- Pay special attention to project tiles and main content regions so they feel compact, controlled, and smooth rather than filling the entire width awkwardly.

## Visual Verification

- Do not rely only on code inspection.
- Always compare the actual rendered UI in Chromium MCP.
- Use screenshots from both mobile and desktop testing as part of the review before closing the task.
