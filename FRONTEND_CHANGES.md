# Frontend Changes Required

This document outlines the frontend changes required due to backend API modifications.

---

## ⚠️ Breaking Changes

### 1. Villages Endpoint Response Structure Change

**Endpoint**: `GET /api/v1/location-details/villages`

#### Before (Old Response Structure)
```typescript
// Response was a direct array
{
  "success": true,
  "data": [
    {
      "village": "Koramangala",
      "customerCount": 15
    },
    {
      "village": "Indiranagar",
      "customerCount": 8
    }
  ]
}
```

#### After (New Response Structure)
```typescript
// Response is now paginated
{
  "success": true,
  "data": {
    "content": [
      {
        "village": "Koramangala",
        "customerCount": 15
      },
      {
        "village": "Indiranagar",
        "customerCount": 8
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 25,
    "totalPages": 2,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### Required Frontend Changes

**TypeScript Interface Updates**:
```typescript
// Old interface
interface VillageResponse {
  village: string;
  customerCount: number;
}

// New interface
interface VillageResponse {
  village: string;
  customerCount: number;
}

interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

// API Response type
interface VillagesApiResponse {
  success: boolean;
  data: PageResponse<VillageResponse>; // Changed from VillageResponse[]
}
```

**Code Changes Required**:

1. **Update API call handlers**:
```typescript
// Before
const response = await fetch('/api/v1/location-details/villages?name=Kora');
const data = await response.json();
const villages = data.data; // Direct array access

// After
const response = await fetch('/api/v1/location-details/villages?name=Kora&page=0&size=20');
const data = await response.json();
const villages = data.data.content; // Access via content property
const pagination = {
  page: data.data.page,
  size: data.data.size,
  totalElements: data.data.totalElements,
  totalPages: data.data.totalPages,
  hasNext: data.data.hasNext,
  hasPrevious: data.data.hasPrevious
};
```

2. **Update components that display villages**:
```typescript
// Before
{villages.map(village => (
  <VillageItem key={village.village} village={village} />
))}

// After
{villages.content.map(village => (
  <VillageItem key={village.village} village={village} />
))}
```

3. **Add pagination controls** (if not already present):
```typescript
// Example pagination component
<Pagination
  currentPage={pagination.page}
  totalPages={pagination.totalPages}
  hasNext={pagination.hasNext}
  hasPrevious={pagination.hasPrevious}
  onPageChange={(page) => fetchVillages(page)}
/>
```

---

## ✨ New Features (Non-Breaking)

### 2. Village Filter for Customers Endpoint

**Endpoint**: `GET /api/v1/owners/{ownerId}/customers`

#### New Optional Query Parameter

**Parameter**: `village` (string, optional)

**Description**: Filter customers by exact village name (case-insensitive)

**Example Usage**:
```typescript
// Get all customers
GET /api/v1/owners/{ownerId}/customers?page=0&size=20

// Get customers from specific village
GET /api/v1/owners/{ownerId}/customers?village=Koramangala&page=0&size=20

// Combine village filter with search
GET /api/v1/owners/{ownerId}/customers?village=Koramangala&q=John&page=0&size=20
```

#### Frontend Implementation

**Add village filter to customer search/filter UI**:

```typescript
// Example: Add village dropdown/input to customer filter component
interface CustomerFilters {
  searchText?: string;
  village?: string; // NEW: Add village filter
  page?: number;
  size?: number;
}

// API call function
async function fetchCustomers(filters: CustomerFilters) {
  const params = new URLSearchParams();
  if (filters.searchText) params.append('q', filters.searchText);
  if (filters.village) params.append('village', filters.village); // NEW
  params.append('page', String(filters.page || 0));
  params.append('size', String(filters.size || 20));
  
  const response = await fetch(`/api/v1/owners/${ownerId}/customers?${params}`);
  return response.json();
}
```

**UI Component Example**:
```tsx
// Add village filter dropdown/autocomplete
<VillageFilter
  value={filters.village}
  onChange={(village) => setFilters({ ...filters, village, page: 0 })}
  placeholder="Filter by village"
/>

// Or use existing village list with selection
<VillageSelect
  villages={villages.content} // From villages endpoint
  selected={filters.village}
  onSelect={(village) => setFilters({ ...filters, village, page: 0 })}
/>
```

---

### 3. Pagination Parameters for Villages Endpoint

**Endpoint**: `GET /api/v1/location-details/villages`

#### New Optional Query Parameters

- `page` (number, optional, default: 0): Page number (0-indexed)
- `size` (number, optional, default: 20): Number of items per page

**Example Usage**:
```typescript
// Get first page (default)
GET /api/v1/location-details/villages

// Get specific page
GET /api/v1/location-details/villages?page=1&size=10

// With name filter and pagination
GET /api/v1/location-details/villages?name=Kora&page=0&size=10
```

#### Frontend Implementation

**Add pagination support**:
```typescript
interface VillageFilters {
  name?: string;
  page?: number; // NEW: Add pagination
  size?: number; // NEW: Add page size
}

async function fetchVillages(filters: VillageFilters) {
  const params = new URLSearchParams();
  if (filters.name) params.append('name', filters.name);
  if (filters.page !== undefined) params.append('page', String(filters.page));
  if (filters.size !== undefined) params.append('size', String(filters.size));
  
  const response = await fetch(`/api/v1/location-details/villages?${params}`);
  return response.json();
}
```

---

## 📋 Migration Checklist

### High Priority (Breaking Changes)

- [ ] **Update villages API response handling**
  - [ ] Change `data` access from array to `data.content`
  - [ ] Update TypeScript interfaces/types
  - [ ] Update all components that consume villages data
  - [ ] Test all village-related features

- [ ] **Add pagination support for villages**
  - [ ] Add pagination controls UI
  - [ ] Implement page/size state management
  - [ ] Update API calls to include pagination parameters
  - [ ] Handle pagination metadata (hasNext, hasPrevious, etc.)

### Medium Priority (New Features)

- [ ] **Add village filter to customer list**
  - [ ] Add village filter UI component (dropdown/autocomplete)
  - [ ] Integrate with existing customer filters
  - [ ] Update API calls to include village parameter
  - [ ] Test village filtering functionality

- [ ] **Update customer list to use village filter**
  - [ ] Connect village selection to customer list refresh
  - [ ] Reset pagination when village filter changes
  - [ ] Add clear/reset filter functionality

### Low Priority (Enhancements)

- [ ] **Optimize village loading**
  - [ ] Implement pagination for large village lists
  - [ ] Add loading states
  - [ ] Add error handling

---

## 🔍 Testing Checklist

### Villages Endpoint
- [ ] Verify response structure matches `PageResponse<VillageResponse>`
- [ ] Test pagination with different page/size values
- [ ] Test name filtering with pagination
- [ ] Verify pagination metadata (hasNext, hasPrevious, totalPages)
- [ ] Test edge cases (empty results, last page, etc.)

### Customers Endpoint
- [ ] Test village filter parameter
- [ ] Verify village filter works with search text
- [ ] Test village filter with pagination
- [ ] Verify exact village name matching (case-insensitive)
- [ ] Test with invalid/non-existent village names

---

## 📝 Code Examples

### Complete Example: Villages with Pagination

```typescript
// types.ts
export interface VillageResponse {
  village: string;
  customerCount: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface VillagesApiResponse {
  success: boolean;
  data: PageResponse<VillageResponse>;
}

// api.ts
export async function fetchVillages(
  name?: string,
  page: number = 0,
  size: number = 20
): Promise<VillagesApiResponse> {
  const params = new URLSearchParams();
  if (name) params.append('name', name);
  params.append('page', String(page));
  params.append('size', String(size));
  
  const response = await fetch(`/api/v1/location-details/villages?${params}`);
  return response.json();
}

// component.tsx
function VillagesList() {
  const [villages, setVillages] = useState<PageResponse<VillageResponse> | null>(null);
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [searchName, setSearchName] = useState('');
  
  useEffect(() => {
    fetchVillages(searchName, page, size).then(response => {
      setVillages(response.data);
    });
  }, [page, size, searchName]);
  
  if (!villages) return <Loading />;
  
  return (
    <div>
      <input
        value={searchName}
        onChange={(e) => {
          setSearchName(e.target.value);
          setPage(0); // Reset to first page on search
        }}
        placeholder="Search villages..."
      />
      
      {villages.content.map(village => (
        <VillageItem key={village.village} village={village} />
      ))}
      
      <Pagination
        currentPage={villages.page}
        totalPages={villages.totalPages}
        hasNext={villages.hasNext}
        hasPrevious={villages.hasPrevious}
        onPageChange={setPage}
      />
    </div>
  );
}
```

### Complete Example: Customers with Village Filter

```typescript
// api.ts
export async function fetchCustomers(
  ownerId: string,
  filters: {
    searchText?: string;
    village?: string;
    page?: number;
    size?: number;
  }
): Promise<CustomersApiResponse> {
  const params = new URLSearchParams();
  if (filters.searchText) params.append('q', filters.searchText);
  if (filters.village) params.append('village', filters.village);
  params.append('page', String(filters.page || 0));
  params.append('size', String(filters.size || 20));
  
  const response = await fetch(`/api/v1/owners/${ownerId}/customers?${params}`);
  return response.json();
}

// component.tsx
function CustomersList({ ownerId }: { ownerId: string }) {
  const [customers, setCustomers] = useState<PageResponse<CustomerResponse> | null>(null);
  const [filters, setFilters] = useState({
    searchText: '',
    village: '',
    page: 0,
    size: 20
  });
  
  useEffect(() => {
    fetchCustomers(ownerId, filters).then(response => {
      setCustomers(response.data);
    });
  }, [ownerId, filters]);
  
  return (
    <div>
      <input
        value={filters.searchText}
        onChange={(e) => setFilters({ ...filters, searchText: e.target.value, page: 0 })}
        placeholder="Search customers..."
      />
      
      <VillageSelect
        value={filters.village}
        onChange={(village) => setFilters({ ...filters, village, page: 0 })}
        placeholder="Filter by village"
      />
      
      {customers?.content.map(customer => (
        <CustomerItem key={customer.id} customer={customer} />
      ))}
      
      <Pagination
        currentPage={customers?.page || 0}
        totalPages={customers?.totalPages || 0}
        hasNext={customers?.hasNext || false}
        hasPrevious={customers?.hasPrevious || false}
        onPageChange={(page) => setFilters({ ...filters, page })}
      />
    </div>
  );
}
```

---

## ⚠️ Important Notes

1. **Backward Compatibility**: The `village` parameter for customers endpoint is optional, so existing code will continue to work without changes.

2. **Default Values**: Both `page` and `size` have defaults (0 and 20 respectively), so existing API calls without these parameters will still work, but the response structure for villages endpoint has changed.

3. **Case Sensitivity**: Village filtering is case-insensitive, so "Koramangala", "koramangala", and "KORAMANGALA" will all match the same village.

4. **Exact Match**: Village filter uses exact match (after trimming), not partial match. Use the `q` parameter for partial text search in customer names/phones.

5. **Pagination Reset**: When changing filters (village, search text), remember to reset pagination to page 0 for better UX.

---

## 📞 Support

If you encounter any issues during migration, please refer to:
- Backend API documentation: `/api/v1/swagger-ui.html`
- Backend changes document: `changes.md`
- Backend code: See modified files in `changes.md`
