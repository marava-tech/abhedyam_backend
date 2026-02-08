# TODO - Backend Improvements

## 1. Village-specific search endpoint
- **Issue**: Using generic `searchCustomers(villageName)` may match customer names/phones containing village name
- **Suggestion**: Add `getCustomersByVillage(String village)` method or use `village` query param if backend supports it
- **Impact**: More accurate results, better performance
- **Status**: Pending

## 2. API without pagination: `getVillages()` - `/api/v1/location-details/villages`
- **Current**: Returns `List<VillageResponse>` without pagination
- **Task**: Implement pagination for `/api/v1/location-details/villages` endpoint
- **Status**: Pending

## 3. Use `village` query param to get customers by village
- **Current**: No dedicated village filter in customer search
- **Task**: Add `village` query param to `/api/v1/owners/{ownerId}/customers` endpoint to filter customers by village instead of creating a separate API
- **Status**: Pending

## 4. Add pagination: `/api/v1/owners/{ownerId}/customers`
- **Current**: Already has pagination implemented (uses `PageResponse`)
- **Status**: ✅ Already implemented
