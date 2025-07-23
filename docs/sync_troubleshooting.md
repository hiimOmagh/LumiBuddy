# Firestore Sync Troubleshooting

Firestore keeps your plants and diary entries consistent across devices. If sync fails, it is usually due to network issues or conflicting updates. This guide lists typical error scenarios and offers tips for resolving them

## Common Firestore Sync Errors

Watch for authentication failures, permission errors in your Firestore rules, or version conflicts when multiple devices modify the same document. These issues typically surface in logcat with a clear error message.

## Conflict Resolution Strategies

When the same record changes on two devices, the last write wins by default. Open the diary entry or plant on the device showing an error and manually merge the changes if something was overwritten.

## Debugging and Logging Tips

Enable verbose logging by setting the `FirebaseFirestore` log level to `DEBUG`. Reviewing timestamps and document IDs in the logs makes it easier to spot failed writes or dropped connections.

## Offline Scenarios and Recovery

Firestore caches writes locally when offline. Once connectivity is restored the queued changes upload automatically. If you suspect a record never synced, open the settings screen and tap **Force Sync** to retry.
